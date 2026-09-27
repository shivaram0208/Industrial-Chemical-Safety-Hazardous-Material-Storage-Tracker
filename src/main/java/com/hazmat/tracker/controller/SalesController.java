package com.hazmat.tracker.controller;

import com.hazmat.tracker.dto.OrderDtos.CreateSalesOrderRequest;
import com.hazmat.tracker.dto.OrderDtos.PaymentRequest;
import com.hazmat.tracker.entity.CustomerInvoice;
import com.hazmat.tracker.entity.Payment;
import com.hazmat.tracker.entity.SalesOrder;
import com.hazmat.tracker.repository.CustomerInvoiceRepository;
import com.hazmat.tracker.repository.SalesOrderRepository;
import com.hazmat.tracker.service.SalesService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;
    private final SalesOrderRepository soRepository;
    private final CustomerInvoiceRepository invoiceRepository;

    public SalesController(SalesService salesService, SalesOrderRepository soRepository,
                            CustomerInvoiceRepository invoiceRepository) {
        this.salesService = salesService;
        this.soRepository = soRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // ---- Sales Orders ----
    @PostMapping("/orders")
    public SalesOrder createOrder(@RequestBody CreateSalesOrderRequest request) {
        return salesService.createSalesOrder(request);
    }

    @GetMapping("/orders")
    public List<SalesOrder> getOrders() {
        return soRepository.findAll();
    }

    @GetMapping("/orders/{id}")
    public SalesOrder getOrder(@PathVariable Long id) {
        return soRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SO not found: " + id));
    }

    @PutMapping("/orders/{id}/confirm")
    public SalesOrder confirmOrder(@PathVariable Long id) {
        return salesService.confirmSalesOrder(id);
    }

    // ---- Customer Invoices ----
    @PostMapping("/orders/{id}/invoice")
    public CustomerInvoice convertToInvoice(@PathVariable Long id,
                                             @RequestParam String invoiceNumber,
                                             @RequestParam(required = false)
                                             @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                                             LocalDate invoiceDate) {
        return salesService.convertToInvoice(id, invoiceNumber, invoiceDate);
    }

    @GetMapping("/invoices")
    public List<CustomerInvoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    @GetMapping("/invoices/{id}")
    public CustomerInvoice getInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
    }

    // ---- Payments (Bank/Cash) ----
    @PostMapping("/invoices/{id}/pay")
    public Payment collectPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return salesService.collectPayment(id, request);
    }

    @GetMapping("/receivables/outstanding")
    public java.math.BigDecimal getOutstandingReceivables() {
        return salesService.getTotalOutstandingReceivables();
    }
}
