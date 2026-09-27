package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.AnalyticAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
}
