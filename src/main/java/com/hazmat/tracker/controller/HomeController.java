package com.hazmat.tracker.controller;

import com.hazmat.tracker.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final ContactRepository contactRepository;
    private final HazmatVaultRepository hazmatVaultRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final VendorBillRepository vendorBillRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final ChemicalIntakeRepository chemicalIntakeRepository;
    private final SafetyInspectionRepository safetyInspectionRepository;

    public HomeController(
            ProductRepository productRepository,
            ContactRepository contactRepository,
            HazmatVaultRepository hazmatVaultRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            SalesOrderRepository salesOrderRepository,
            VendorBillRepository vendorBillRepository,
            CustomerInvoiceRepository customerInvoiceRepository,
            ChemicalIntakeRepository chemicalIntakeRepository,
            SafetyInspectionRepository safetyInspectionRepository) {

        this.productRepository = productRepository;
        this.contactRepository = contactRepository;
        this.hazmatVaultRepository = hazmatVaultRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.customerInvoiceRepository = customerInvoiceRepository;
        this.chemicalIntakeRepository = chemicalIntakeRepository;
        this.safetyInspectionRepository = safetyInspectionRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("contactCount", contactRepository.count());
        model.addAttribute("vaultCount", hazmatVaultRepository.count());
        model.addAttribute("purchaseCount", purchaseOrderRepository.count());
        model.addAttribute("salesCount", salesOrderRepository.count());
        model.addAttribute("billCount", vendorBillRepository.count());
        model.addAttribute("invoiceCount", customerInvoiceRepository.count());
        model.addAttribute("intakeCount", chemicalIntakeRepository.count());
        model.addAttribute("inspectionCount", safetyInspectionRepository.count());

        return "dashboard";
    }
}