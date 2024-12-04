package com.example.project1.data.pipeline.impl;

import com.example.project1.data.pipeline.Filter;
import com.example.project1.db.StockIssuerEntity;
import com.example.project1.repository.StockIssuerRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class F1Stock implements Filter<List<StockIssuerEntity>> {

    private final StockIssuerRepository stockIssuerRepository;

    public F1Stock(StockIssuerRepository stockIssuerRepository) {
        this.stockIssuerRepository = stockIssuerRepository;
    }

    private static final String STOCK_MARKET_URL = "https://www.mse.mk/mk/stats/symbolhistory/kmb";

    @Override
    public List<StockIssuerEntity> execute(List<StockIssuerEntity> input) throws IOException {
        Document document = Jsoup.connect(STOCK_MARKET_URL).get();
        Element selectMenu = document.select("select#Code").first();

        if (selectMenu != null) {
            Elements options = selectMenu.select("option");
            for (Element option : options) {
                String code = option.attr("value");
                if (!code.isEmpty() && code.matches("^[a-zA-Z]+$")) {
                    if (stockIssuerRepository.findByCompanyCode(code).isEmpty()) {
                        stockIssuerRepository.save(new StockIssuerEntity(code));
                    }
                }
            }
        }

        return stockIssuerRepository.findAll();
    }
}
