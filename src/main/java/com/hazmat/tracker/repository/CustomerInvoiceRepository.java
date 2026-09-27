package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
}
