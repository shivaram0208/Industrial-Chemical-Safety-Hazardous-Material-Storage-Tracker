package com.hazmat.tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderDtos {

    public static class LineItem {
        public Long productId;
        public BigDecimal quantity;
        public BigDecimal unitPrice;
    }

    public static class CreatePurchaseOrderRequest {
        public String poNumber;
        public Long vendorId;
        public LocalDate orderDate;
        public List<LineItem> lines;
    }

    public static class CreateSalesOrderRequest {
        public String soNumber;
        public Long customerId;
        public LocalDate orderDate;
        public List<LineItem> lines;
    }

    public static class PaymentRequest {
        public String paymentNumber;
        public BigDecimal amount;
        public LocalDate paymentDate;
        public String method; // CASH or BANK
    }

    public static class IntakeRequest {
        public Long vaultId;
        public Long customerId;
        public String lotNumber;
        public String chemicalName;
        public BigDecimal quantity;
        public LocalDate intakeDate;
        public LocalDate expiryDate;
        public String hazardClass;
    }
}
