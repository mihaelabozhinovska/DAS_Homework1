package com.example.project1.data.pipeline.impl;

import com.example.project1.data.PriceSnapshotParser;
import com.example.project1.data.pipeline.Filter;
import com.example.project1.db.StockIssuerEntity;
import com.example.project1.db.PriceSnapshotEntity;
import com.example.project1.repository.StockIssuerRepository;
import com.example.project1.repository.PriceSnapshotRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class F2Price implements Filter<List<StockIssuerEntity>> {

    private final StockIssuerRepository stockIssuerRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;

    private static final String HISTORICAL_DATA_URL = "https://www.mse.mk/mk/stats/symbolhistory/";

    public F2Price(StockIssuerRepository stockIssuerRepository, PriceSnapshotRepository priceSnapshotRepository) {
        this.stockIssuerRepository = stockIssuerRepository;
        this.priceSnapshotRepository = priceSnapshotRepository;
    }

    public List<StockIssuerEntity> execute(List<StockIssuerEntity> input) throws IOException {
        List<StockIssuerEntity> companies = new ArrayList<>();

        for (StockIssuerEntity company : input) {
            if (company.getLastUpdated() == null) {
                for (int i = 1; i <= 10; i++) {
                    int temp = i - 1;
                    LocalDate fromDate = LocalDate.now().minusYears(i);
                    LocalDate toDate = LocalDate.now().minusYears(temp);
                    addHistoricalData(company, fromDate, toDate);
                }
            } else {
                companies.add(company);
            }
        }

        return companies;
    }

    private void addHistoricalData(StockIssuerEntity company, LocalDate fromDate, LocalDate toDate) throws IOException {
        Connection.Response response = Jsoup.connect(HISTORICAL_DATA_URL + company.getCompanyCode())
                .data("FromDate", fromDate.toString())
                .data("ToDate", toDate.toString())
                .method(Connection.Method.POST)
                .execute();

        Document document = response.parse();

        Element table = document.select("table#resultsTable").first();

        if (table != null) {
            Elements rows = table.select("tbody tr");

            for (Element row : rows) {
                Elements columns = row.select("td");

                if (columns.size() > 0) {
                    LocalDate date = PriceSnapshotParser.parseDate(columns.get(0).text(), "d.M.yyyy");

                    if (priceSnapshotRepository.findByDateAndCompany(date, company).isEmpty()) {


                        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

                        Double lastTransactionPrice = PriceSnapshotParser.parseDouble(columns.get(1).text(), format);
                        Double maxPrice = PriceSnapshotParser.parseDouble(columns.get(2).text(), format);
                        Double minPrice = PriceSnapshotParser.parseDouble(columns.get(3).text(), format);
                        Double averagePrice = PriceSnapshotParser.parseDouble(columns.get(4).text(), format);
                        Double percentageChange = PriceSnapshotParser.parseDouble(columns.get(5).text(), format);
                        Integer quantity = PriceSnapshotParser.parseInteger(columns.get(6).text(), format);
                        Integer turnoverBest = PriceSnapshotParser.parseInteger(columns.get(7).text(), format);
                        Integer totalTurnover = PriceSnapshotParser.parseInteger(columns.get(8).text(), format);

                        if (maxPrice != null) {

                            if (company.getLastUpdated() == null || company.getLastUpdated().isBefore(date)) {
                                company.setLastUpdated(date);
                            }

                            PriceSnapshotEntity priceSnapshotEntity = new PriceSnapshotEntity(
                                    date, lastTransactionPrice, maxPrice, minPrice, averagePrice, percentageChange,
                                    quantity, turnoverBest, totalTurnover);
                            priceSnapshotEntity.setCompany(company);
                            priceSnapshotRepository.save(priceSnapshotEntity);
                            company.getHistoricalData().add(priceSnapshotEntity);
                        }
                    }
                }
            }
        }

        stockIssuerRepository.save(company);
    }

}
