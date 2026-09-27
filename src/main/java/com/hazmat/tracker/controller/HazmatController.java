package com.hazmat.tracker.controller;

import com.hazmat.tracker.dto.OrderDtos.IntakeRequest;
import com.hazmat.tracker.entity.ChemicalIntake;
import com.hazmat.tracker.entity.HazmatVault;
import com.hazmat.tracker.entity.SafetyInspection;
import com.hazmat.tracker.repository.HazmatVaultRepository;
import com.hazmat.tracker.repository.SafetyInspectionRepository;
import com.hazmat.tracker.service.HazmatVaultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hazmat")
public class HazmatController {

    private final HazmatVaultRepository vaultRepository;
    private final SafetyInspectionRepository inspectionRepository;
    private final HazmatVaultService vaultService;

    public HazmatController(HazmatVaultRepository vaultRepository,
                             SafetyInspectionRepository inspectionRepository,
                             HazmatVaultService vaultService) {
        this.vaultRepository = vaultRepository;
        this.inspectionRepository = inspectionRepository;
        this.vaultService = vaultService;
    }

    // ---- Vaults ----
    @PostMapping("/vaults")
    public HazmatVault createVault(@RequestBody HazmatVault vault) {
        return vaultRepository.save(vault);
    }

    @GetMapping("/vaults")
    public List<HazmatVault> getVaults() {
        return vaultRepository.findAll();
    }

    @GetMapping("/vaults/{id}")
    public HazmatVault getVault(@PathVariable Long id) {
        return vaultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vault not found: " + id));
    }

    // ---- Intake / Expiry ----
    @PostMapping("/intake")
    public ChemicalIntake logIntake(@RequestBody IntakeRequest request) {
        return vaultService.logIntake(request);
    }

    @GetMapping("/expiring")
    public List<ChemicalIntake> getExpiring(@RequestParam(defaultValue = "30") int days) {
        return vaultService.getExpiringWithinDays(days);
    }

    @GetMapping("/expired")
    public List<ChemicalIntake> getExpired() {
        return vaultService.getAlreadyExpired();
    }

    @GetMapping("/vaults/{id}/intakes")
    public List<ChemicalIntake> getIntakesForVault(@PathVariable Long id) {
        return vaultService.getIntakesForVault(id);
    }

    // ---- Safety Inspections ----
    @PostMapping("/inspections")
    public SafetyInspection scheduleInspection(@RequestBody SafetyInspection inspection) {
        return inspectionRepository.save(inspection);
    }

    @GetMapping("/inspections")
    public List<SafetyInspection> getInspections() {
        return inspectionRepository.findAll();
    }

    @PutMapping("/inspections/{id}/complete")
    public SafetyInspection completeInspection(@PathVariable Long id, @RequestBody(required = false) String remarks) {
        SafetyInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found: " + id));
        inspection.setStatus(com.hazmat.tracker.entity.Enums.InspectionStatus.COMPLETED);
        inspection.setCompletedDate(java.time.LocalDate.now());
        if (remarks != null) inspection.setRemarks(remarks);
        return inspectionRepository.save(inspection);
    }
}
