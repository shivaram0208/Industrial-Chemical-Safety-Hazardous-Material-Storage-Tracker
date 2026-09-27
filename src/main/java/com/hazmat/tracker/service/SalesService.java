package com.hazmat.tracker.service;

import com.hazmat.tracker.dto.OrderDtos.CreateSalesOrderRequest;
import com.hazmat.tracker.dto.OrderDtos.PaymentRequest;
import com.hazmat.tracker.entity.*;
import com.hazmat.tracker.entity.Enums.*;
import com.hazmat.tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class SalesService {

    private final SalesOrderRepository soRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final AccountingService accountingService;

    public SalesService(SalesOrderRepository soRepository, CustomerInvoiceRepository invoiceRepository,
                         PaymentRepository paymentRepository, ContactRepository contactRepository,
                         ProductRepository productRepository, AccountRepository accountRepository,
                         AccountingService accountingService) {
        this.soRepository = soRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.accountingService = accountingService;
    }

    @Transactional
    public SalesOrder createSalesOrder(CreateSalesOrderRequest req) {
        Contact customer = contactRepository.findById(req.customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId));

        SalesOrder so = new SalesOrder();
        so.setSoNumber(req.soNumber);
        so.setCustomer(customer);
        so.setOrderDate(req.orderDate != null ? req.orderDate : LocalDate.now());
        so.setStatus(OrderStatus.DRAFT);

        req.lines.forEach(l -> {
            Product product = productRepository.findById(l.productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + l.productId));
            SalesOrderLine line = new SalesOrderLine();
            line.setSalesOrder(so);
            line.setProduct(product);
            line.setQuantity(l.quantity);
            line.setUnitPrice(l.unitPrice != null ? l.unitPrice : product.getUnitPrice());
            so.getLines().add(line);
        });

        return soRepository.save(so);
    }

    @Transactional
    public SalesOrder confirmSalesOrder(Long soId) {
        SalesOrder so = soRepository.findById(soId)
                .orElseThrow(() -> new IllegalArgumentException("SO not found: " + soId));
        so.setStatus(OrderStatus.CONFIRMED);
        return soRepository.save(so);
    }

    /**
     * Converts a confirmed SO into a Customer Invoice and posts the Sales journal entry:
     * Debit: Containment Storage Facility / Accounts Receivable (Asset)
     * Credit: Hazmat Storage Revenue / Compliance Fee Revenue (Income)
     */
    @Transactional
    public CustomerInvoice convertToInvoice(Long soId, String invoiceNumber, LocalDate invoiceDate) {
        SalesOrder so = soRepository.findById(soId)
                .orElseThrow(() -> new IllegalArgumentException("SO not found: " + soId));

        CustomerInvoice invoice = new CustomerInvoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setSalesOrder(so);
        invoice.setCustomer(so.getCustomer());
        invoice.setInvoiceDate(invoiceDate != null ? invoiceDate : LocalDate.now());
        invoice.setTotalAmount(so.getTotalAmount());
        invoice.setStatus(OrderStatus.BILLED_OR_INVOICED);

        so.setStatus(OrderStatus.BILLED_OR_INVOICED);
        soRepository.save(so);
        CustomerInvoice saved = invoiceRepository.save(invoice);

        Account receivableAccount = accountRepository.findByCode("1001")
                .orElseThrow(() -> new IllegalStateException("Storage facility/receivable account 1001 not seeded"));
        Account revenueAccount = accountRepository.findByCode("4001")
                .orElseThrow(() -> new IllegalStateException("Revenue account 4001 not seeded"));

        accountingService.postEntry(JournalType.SALES, saved.getInvoiceDate(), saved.getInvoiceNumber(),
                "Customer invoice for SO " + so.getSoNumber(), receivableAccount, revenueAccount, saved.getTotalAmount());

        return saved;
    }

    /**
     * Collects payment for a customer invoice via Bank/Cash journal:
     * Debit: Cash/Bank (Asset) — increases asset
     * Credit: Containment Storage Facility / Accounts Receivable (Asset) — reduces receivable
     */
    @Transactional
    public Payment collectPayment(Long invoiceId, PaymentRequest req) {
        CustomerInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        Payment payment = new Payment();
        payment.setPaymentNumber(req.paymentNumber);
        payment.setCustomerInvoice(invoice);
        payment.setDirection(PaymentDirection.INBOUND);
        payment.setMethod(req.method != null ? PaymentMethod.valueOf(req.method) : PaymentMethod.BANK);
        payment.setAmount(req.amount);
        payment.setPaymentDate(req.paymentDate != null ? req.paymentDate : LocalDate.now());

        invoice.setAmountReceived(invoice.getAmountReceived().add(req.amount));
        if (invoice.getAmountReceived().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(OrderStatus.PAID);
        }
        invoiceRepository.save(invoice);
        Payment saved = paymentRepository.save(payment);

        Account bankAccount = accountRepository.findByCode("1002")
                .orElseThrow(() -> new IllegalStateException("Bank account 1002 not seeded"));
        Account receivableAccount = accountRepository.findByCode("1001")
                .orElseThrow(() -> new IllegalStateException("Receivable account 1001 not seeded"));

        JournalType journalType = payment.getMethod() == PaymentMethod.CASH ? JournalType.CASH : JournalType.BANK;
        accountingService.postEntry(journalType, payment.getPaymentDate(), payment.getPaymentNumber(),
                "Payment received for invoice " + invoice.getInvoiceNumber(), bankAccount, receivableAccount, payment.getAmount());

        return saved;
    }

    public BigDecimal getTotalOutstandingReceivables() {
        return invoiceRepository.findAll().stream()
                .map(i -> i.getTotalAmount().subtract(i.getAmountReceived()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
