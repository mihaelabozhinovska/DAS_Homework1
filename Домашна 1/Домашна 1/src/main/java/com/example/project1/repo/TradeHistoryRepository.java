package com.example.project1.repo;

import com.example.project1.model.TradeHistory;
import com.example.project1.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
    Optional<TradeHistory> findByDateAndCompany(LocalDate date, Profile profile);
}
