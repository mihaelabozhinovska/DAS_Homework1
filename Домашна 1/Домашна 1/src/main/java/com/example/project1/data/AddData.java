package com.example.project1.data;

import com.example.project1.data.pipeline.Pipe;
import com.example.project1.data.pipeline.impl.FilterCompanyData;
import com.example.project1.data.pipeline.impl.FilterReAddCompanyDetails;
import com.example.project1.data.pipeline.impl.FilterCompanyDetails;
import com.example.project1.model.Profile;
import com.example.project1.repo.ProfileRepository;
import com.example.project1.repo.TradeHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddData {

    private final ProfileRepository profileRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    @PostConstruct
    private void initializeData() throws IOException, ParseException {
        long startTime = System.nanoTime();
        Pipe<List<Profile>> pipe = new Pipe<>();
        pipe.addFilter(new FilterCompanyData(profileRepository));
        pipe.addFilter(new FilterReAddCompanyDetails(profileRepository, tradeHistoryRepository));
        pipe.addFilter(new FilterCompanyDetails(profileRepository, tradeHistoryRepository));
        pipe.runFilter(null);
        long endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1_000_000;

        long hours = durationInMillis / 3_600_000;
        long minutes = (durationInMillis % 3_600_000) / 60_000;
        long seconds = (durationInMillis % 60_000) / 1_000;

        System.out.printf("Total time for all filters to complete: %02d hours, %02d minutes, %02d seconds%n",
                hours, minutes, seconds);

    }

}
