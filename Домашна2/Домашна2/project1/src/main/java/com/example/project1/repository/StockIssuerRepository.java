package com.example.project1.repository;

import com.example.project1.db.StockIssuerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockIssuerRepository extends JpaRepository<StockIssuerEntity, Long> {
    Optional<StockIssuerEntity> findByCompanyCode(String companyCode);
}
