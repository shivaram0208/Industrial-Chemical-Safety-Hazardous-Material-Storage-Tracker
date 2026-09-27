package com.hazmat.tracker.service;

import com.hazmat.tracker.dto.OrderDtos.CreatePurchaseOrderRequest;
import com.hazmat.tracker.dto.OrderDtos.PaymentRequest;
import com.hazmat.tracker.entity.*;
import com.hazmat.tracker.entity.Enums.*;
import com.hazmat.tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PurchaseService {

    private final PurchaseOrderRepository poRepository;
    private final VendorBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final AccountingService accountingService;

    public PurchaseService(PurchaseOrderRepository poRepository, VendorBillRepository billRepository,
                            PaymentRepository paymentRepository, ContactRepository contactRepository,
                            ProductRepository productRepository, AccountRepository accountRepository,
                            AccountingService accountingService) {
        this.poRepository = poRepository;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.accountingService = accountingService;
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(CreatePurchaseOrderRequest req) {
        Contact vendor = contactRepository.findById(req.vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found: " + req.vendorId));

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(req.poNumber);
        po.setVendor(vendor);
        po.setOrderDate(req.orderDate != null ? req.orderDate : LocalDate.now());
        po.setStatus(OrderStatus.DRAFT);

        req.lines.forEach(l -> {
            Product product = productRepository.findById(l.productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + l.productId));
            PurchaseOrderLine line = new PurchaseOrderLine();
            line.setPurchaseOrder(po);
            line.setProduct(product);
            line.setQuantity(l.quantity);
            line.setUnitPrice(l.unitPrice != null ? l.unitPrice : product.getUnitPrice());
            po.getLines().add(line);
        });

        return poRepository.save(po);
    }

    @Transactional
    public PurchaseOrder confirmPurchaseOrder(Long poId) {
        PurchaseOrder po = poRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));
        po.setStatus(OrderStatus.CONFIRMED);
        return poRepository.save(po);
    }

    /**
     * Converts a confirmed PO into a Vendor Bill and posts the Purchase journal entry:
     * Debit: Chemical Disposal / Facility Safety Operating Costs (Expense)
     * Credit: Chemical Supplier Payables (Liability)
     */
    @Transactional
    public VendorBill convertToVendorBill(Long poId, String billNumber, LocalDate billDate) {
        PurchaseOrder po = poRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));

        VendorBill bill = new VendorBill();
        bill.setBillNumber(billNumber);
        bill.setPurchaseOrder(po);
        bill.setVendor(po.getVendor());
        bill.setBillDate(billDate != null ? billDate : LocalDate.now());
        bill.setTotalAmount(po.getTotalAmount());
        bill.setStatus(OrderStatus.BILLED_OR_INVOICED);

        po.setStatus(OrderStatus.BILLED_OR_INVOICED);
        poRepository.save(po);
        VendorBill saved = billRepository.save(bill);

        Account expenseAccount = accountRepository.findByCode("5001")
                .orElseThrow(() -> new IllegalStateException("Expense account 5001 not seeded"));
        Account payablesAccount = accountRepository.findByCode("2001")
                .orElseThrow(() -> new IllegalStateException("Payables account 2001 not seeded"));

        accountingService.postEntry(JournalType.PURCHASE, saved.getBillDate(), saved.getBillNumber(),
                "Vendor bill for PO " + po.getPoNumber(), expenseAccount, payablesAccount, saved.getTotalAmount());

        return saved;
    }

    /**
     * Pays a vendor bill via Bank/Cash journal:
     * Debit: Chemical Supplier Payables (Liability) — reduces liability
     * Credit: Cash/Bank (Asset) — reduces asset
     */
    @Transactional
    public Payment payVendorBill(Long billId, PaymentRequest req) {
        VendorBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor bill not found: " + billId));

        Payment payment = new Payment();
        payment.setPaymentNumber(req.paymentNumber);
        payment.setVendorBill(bill);
        payment.setDirection(PaymentDirection.OUTBOUND);
        payment.setMethod(req.method != null ? PaymentMethod.valueOf(req.method) : PaymentMethod.BANK);
        payment.setAmount(req.amount);
        payment.setPaymentDate(req.paymentDate != null ? req.paymentDate : LocalDate.now());

        bill.setAmountPaid(bill.getAmountPaid().add(req.amount));
        if (bill.getAmountPaid().compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(OrderStatus.PAID);
        }
        billRepository.save(bill);
        Payment saved = paymentRepository.save(payment);

        Account payablesAccount = accountRepository.findByCode("2001")
                .orElseThrow(() -> new IllegalStateException("Payables account 2001 not seeded"));
        Account bankAccount = accountRepository.findByCode("1002")
                .orElseThrow(() -> new IllegalStateException("Bank account 1002 not seeded"));

        JournalType journalType = payment.getMethod() == PaymentMethod.CASH ? JournalType.CASH : JournalType.BANK;
        accountingService.postEntry(journalType, payment.getPaymentDate(), payment.getPaymentNumber(),
                "Payment for bill " + bill.getBillNumber(), payablesAccount, bankAccount, payment.getAmount());

        return saved;
    }

    public BigDecimal getTotalOutstandingPayables() {
        return billRepository.findAll().stream()
                .map(b -> b.getTotalAmount().subtract(b.getAmountPaid()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
