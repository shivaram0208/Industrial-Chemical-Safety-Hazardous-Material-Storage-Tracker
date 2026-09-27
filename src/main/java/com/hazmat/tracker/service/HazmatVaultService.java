package com.hazmat.tracker.service;

import com.hazmat.tracker.dto.OrderDtos.IntakeRequest;
import com.hazmat.tracker.entity.ChemicalIntake;
import com.hazmat.tracker.entity.Contact;
import com.hazmat.tracker.entity.HazmatVault;
import com.hazmat.tracker.repository.ChemicalIntakeRepository;
import com.hazmat.tracker.repository.ContactRepository;
import com.hazmat.tracker.repository.HazmatVaultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HazmatVaultService {

    private final HazmatVaultRepository vaultRepository;
    private final ChemicalIntakeRepository intakeRepository;
    private final ContactRepository contactRepository;

    public HazmatVaultService(HazmatVaultRepository vaultRepository,
                               ChemicalIntakeRepository intakeRepository,
                               ContactRepository contactRepository) {
        this.vaultRepository = vaultRepository;
        this.intakeRepository = intakeRepository;
        this.contactRepository = contactRepository;
    }

    @Transactional
    public ChemicalIntake logIntake(IntakeRequest req) {
        HazmatVault vault = vaultRepository.findById(req.vaultId)
                .orElseThrow(() -> new IllegalArgumentException("Vault not found: " + req.vaultId));
        Contact customer = contactRepository.findById(req.customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId));

        if (vault.getAvailableCapacity().compareTo(req.quantity) < 0) {
            throw new IllegalStateException("Insufficient vault capacity. Available: "
                    + vault.getAvailableCapacity() + ", requested: " + req.quantity);
        }

        ChemicalIntake intake = new ChemicalIntake();
        intake.setVault(vault);
        intake.setCustomer(customer);
        intake.setLotNumber(req.lotNumber);
        intake.setChemicalName(req.chemicalName);
        intake.setQuantity(req.quantity);
        intake.setIntakeDate(req.intakeDate != null ? req.intakeDate : LocalDate.now());
        intake.setExpiryDate(req.expiryDate);
        intake.setHazardClass(req.hazardClass);

        vault.setUsedCapacity(vault.getUsedCapacity().add(req.quantity));
        vaultRepository.save(vault);

        return intakeRepository.save(intake);
    }

    public List<ChemicalIntake> getExpiringWithinDays(int days) {
        LocalDate cutoff = LocalDate.now().plusDays(days);
        return intakeRepository.findByExpiryDateBetween(LocalDate.now(), cutoff);
    }

    public List<ChemicalIntake> getAlreadyExpired() {
        return intakeRepository.findByExpiryDateBefore(LocalDate.now());
    }

    public List<ChemicalIntake> getIntakesForVault(Long vaultId) {
        return intakeRepository.findByVaultId(vaultId);
    }
}
