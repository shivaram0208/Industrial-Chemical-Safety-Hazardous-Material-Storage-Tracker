package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.HazmatVault;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HazmatVaultRepository extends JpaRepository<HazmatVault, Long> {
}
