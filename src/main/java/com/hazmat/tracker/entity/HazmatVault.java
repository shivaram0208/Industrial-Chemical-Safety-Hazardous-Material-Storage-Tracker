package com.hazmat.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "hazmat_vaults")
@Getter
@Setter
public class HazmatVault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vaultCode; // e.g. VAULT-A1

    @Column(nullable = false)
    private String zone; // e.g. "Sector 4"

    @Column(nullable = false)
    private BigDecimal totalCapacity; // in litres/kg

    @Column(nullable = false)
    private BigDecimal usedCapacity = BigDecimal.ZERO;

    private String hazardClassAllowed; // flammable, corrosive, etc.

    @Transient
    public BigDecimal getAvailableCapacity() {
        return totalCapacity.subtract(usedCapacity);
    }
}
