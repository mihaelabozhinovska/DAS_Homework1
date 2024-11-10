package com.example.project1.data.pipeline.impl;

import com.example.project1.data.TransformData;
import com.example.project1.data.pipeline.Filter;
import com.example.project1.model.TradeHistory;
import com.example.project1.model.Profile;
import com.example.project1.repo.ProfileRepository;
import com.example.project1.repo.TradeHistoryRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class FilterCompanyDetails implements Filter<List<Profile>> {

    private final ProfileRepository profileRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    private static final String DATA_URL = "https://www.mse.mk/mk/stats/symbolhistory/";

    public FilterCompanyDetails(ProfileRepository profileRepository, TradeHistoryRepository tradeHistoryRepository) {
        this.profileRepository = profileRepository;
        this.tradeHistoryRepository = tradeHistoryRepository;
    }

    public List<Profile> execute(List<Profile> input) throws IOException, ParseException {

        for (Profile profile : input) {
            LocalDate fromDate = LocalDate.now();
            LocalDate toDate = LocalDate.now().plusYears(1);
            addHistoricalData(profile, fromDate, toDate);
        }

        return null;
    }

    private void addHistoricalData(Profile profile, LocalDate fromDate, LocalDate toDate) throws IOException, ParseException {
        Connection.Response response = Jsoup.connect(DATA_URL + profile.getCompanyCode())
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
                    LocalDate date = TransformData.dateParser(columns.get(0).text(), "d.M.yyyy");

                    if (date != null && tradeHistoryRepository.findByDateAndCompany(date, profile).isEmpty()) {
                        if (profile.getLastUpdated() == null || profile.getLastUpdated().isBefore(date)) {
                            profile.setLastUpdated(date);
                            profileRepository.save(profile);
                        }

                        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

                        Double lastTransactionPrice = TransformData.doubleParser(columns.get(1).text(), format);
                        Double maxPrice = TransformData.doubleParser(columns.get(2).text(), format);
                        Double minPrice = TransformData.doubleParser(columns.get(3).text(), format);
                        Double averagePrice = TransformData.doubleParser(columns.get(4).text(), format);
                        Double percentageChange = TransformData.doubleParser(columns.get(5).text(), format);
                        Integer quantity = TransformData.integerParser(columns.get(6).text(), format);
                        Integer turnoverBest = TransformData.integerParser(columns.get(7).text(), format);
                        Integer totalTurnover = TransformData.integerParser(columns.get(8).text(), format);

                        TradeHistory tradeHistory = new TradeHistory(
                                date, lastTransactionPrice, maxPrice, minPrice, averagePrice, percentageChange,
                                quantity, turnoverBest, totalTurnover);
                        tradeHistory.setProfile(profile);

                        tradeHistoryRepository.save(tradeHistory);
                    }
                }
            }
        }
    }


}
