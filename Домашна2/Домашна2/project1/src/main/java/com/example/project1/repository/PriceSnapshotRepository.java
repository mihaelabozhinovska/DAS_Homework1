package com.example.project1.repository;

import com.example.project1.db.StockIssuerEntity;
import com.example.project1.db.PriceSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshotEntity, Long> {
    Optional<PriceSnapshotEntity> findByDateAndCompany(LocalDate date, StockIssuerEntity company);
    List<PriceSnapshotEntity> findByCompanyIdAndDateBetween(Long companyId, LocalDate from, LocalDate to);
    List<PriceSnapshotEntity> findAllByDate(LocalDate date);
}
