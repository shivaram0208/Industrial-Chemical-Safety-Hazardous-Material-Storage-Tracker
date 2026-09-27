package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.SafetyInspection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyInspectionRepository extends JpaRepository<SafetyInspection, Long> {
}
