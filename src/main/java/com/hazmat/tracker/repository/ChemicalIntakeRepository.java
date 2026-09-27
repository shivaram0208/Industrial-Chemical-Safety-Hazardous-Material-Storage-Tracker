package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.ChemicalIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChemicalIntakeRepository extends JpaRepository<ChemicalIntake, Long> {
    List<ChemicalIntake> findByExpiryDateBefore(LocalDate date);
    List<ChemicalIntake> findByExpiryDateBetween(LocalDate start, LocalDate end);
    List<ChemicalIntake> findByVaultId(Long vaultId);
}
