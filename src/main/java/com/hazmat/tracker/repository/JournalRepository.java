package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<Journal, Long> {
}
