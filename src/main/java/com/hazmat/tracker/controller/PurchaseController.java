package com.hazmat.tracker.controller;

import com.hazmat.tracker.dto.OrderDtos.CreatePurchaseOrderRequest;
import com.hazmat.tracker.dto.OrderDtos.PaymentRequest;
import com.hazmat.tracker.entity.Payment;
import com.hazmat.tracker.entity.PurchaseOrder;
import com.hazmat.tracker.entity.VendorBill;
import com.hazmat.tracker.repository.PurchaseOrderRepository;
import com.hazmat.tracker.repository.VendorBillRepository;
import com.hazmat.tracker.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseOrderRepository poRepository;
    private final VendorBillRepository billRepository;

    public PurchaseController(PurchaseService purchaseService, PurchaseOrderRepository poRepository,
                               VendorBillRepository billRepository) {
        this.purchaseService = purchaseService;
        this.poRepository = poRepository;
        this.billRepository = billRepository;
    }

    // ---- Purchase Orders ----
    @PostMapping("/orders")
    public PurchaseOrder createOrder(@RequestBody CreatePurchaseOrderRequest request) {
        return purchaseService.createPurchaseOrder(request);
    }

    @GetMapping("/orders")
    public List<PurchaseOrder> getOrders() {
        return poRepository.findAll();
    }

    @GetMapping("/orders/{id}")
    public PurchaseOrder getOrder(@PathVariable Long id) {
        return poRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + id));
    }

    @PutMapping("/orders/{id}/confirm")
    public PurchaseOrder confirmOrder(@PathVariable Long id) {
        return purchaseService.confirmPurchaseOrder(id);
    }

    // ---- Vendor Bills ----
    @PostMapping("/orders/{id}/bill")
    public VendorBill convertToBill(@PathVariable Long id,
                                     @RequestParam String billNumber,
                                     @RequestParam(required = false)
                                     @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                                     LocalDate billDate) {
        return purchaseService.convertToVendorBill(id, billNumber, billDate);
    }

    @GetMapping("/bills")
    public List<VendorBill> getBills() {
        return billRepository.findAll();
    }

    @GetMapping("/bills/{id}")
    public VendorBill getBill(@PathVariable Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + id));
    }

    // ---- Payments (Bank/Cash) ----
    @PostMapping("/bills/{id}/pay")
    public Payment payBill(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return purchaseService.payVendorBill(id, request);
    }

    @GetMapping("/payables/outstanding")
    public java.math.BigDecimal getOutstandingPayables() {
        return purchaseService.getTotalOutstandingPayables();
    }
}
