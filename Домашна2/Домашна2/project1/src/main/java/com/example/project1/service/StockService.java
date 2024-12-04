package com.example.project1.service;

import com.example.project1.db.StockIssuerEntity;
import com.example.project1.db.PriceSnapshotEntity;
import com.example.project1.repository.PriceSnapshotRepository;
import com.example.project1.repository.StockIssuerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockIssuerRepository stockIssuerRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;

    public List<StockIssuerEntity> findAll() {
        return stockIssuerRepository.findAll();
    }

    public StockIssuerEntity findById(Long id) throws Exception {
        return stockIssuerRepository.findById(id).orElseThrow(Exception::new);
    }

    public List<PriceSnapshotEntity> findAllToday() {
        return priceSnapshotRepository.findAllByDate(LocalDate.now());
    }

}
