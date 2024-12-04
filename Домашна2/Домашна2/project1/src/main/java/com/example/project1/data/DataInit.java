package com.example.project1.data;

import com.example.project1.data.pipeline.Pipe;
import com.example.project1.data.pipeline.impl.F1Stock;
import com.example.project1.data.pipeline.impl.F2Price;
import com.example.project1.data.pipeline.impl.F3Price;
import com.example.project1.db.StockIssuerEntity;
import com.example.project1.repository.StockIssuerRepository;
import com.example.project1.repository.PriceSnapshotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final StockIssuerRepository stockIssuerRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;

    @PostConstruct
    private void initializeData() throws IOException, ParseException {
        long startTime = System.nanoTime();

        Pipe<List<StockIssuerEntity>> pipe = new Pipe<>();
        pipe.addFilter(new F1Stock(stockIssuerRepository));
        pipe.addFilter(new F2Price(stockIssuerRepository, priceSnapshotRepository));
        pipe.addFilter(new F3Price(stockIssuerRepository, priceSnapshotRepository));
        pipe.runFilter(null);

        long endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1_000_000;

        long hours = durationInMillis / 3_600_000;
        long minutes = (durationInMillis % 3_600_000) / 60_000;
        long seconds = (durationInMillis % 60_000) / 1_000;

        System.out.printf("Total time for all filters to complete: %02d hours, %02d minutes, %02d seconds%n", hours, minutes, seconds);
    }

}
