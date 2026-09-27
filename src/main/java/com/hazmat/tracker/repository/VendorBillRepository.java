package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
}
