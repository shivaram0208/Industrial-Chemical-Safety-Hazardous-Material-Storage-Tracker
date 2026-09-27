package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
