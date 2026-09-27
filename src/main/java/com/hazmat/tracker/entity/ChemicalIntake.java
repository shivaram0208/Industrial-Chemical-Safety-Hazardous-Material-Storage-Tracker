package com.hazmat.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "chemical_intakes")
@Getter
@Setter
public class ChemicalIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vault_id", nullable = false)
    private HazmatVault vault;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Contact customer; // chemical manufacturer who owns this lot

    @Column(nullable = false)
    private String lotNumber;

    @Column(nullable = false)
    private String chemicalName;

    @Column(nullable = false)
    private BigDecimal quantity; // litres/kg

    @Column(nullable = false)
    private LocalDate intakeDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private String hazardClass;
}
