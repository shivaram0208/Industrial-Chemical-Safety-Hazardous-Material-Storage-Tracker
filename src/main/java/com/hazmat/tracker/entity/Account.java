package com.hazmat.tracker.entity;

import com.hazmat.tracker.entity.Enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. 1000, 2000...

    @Column(nullable = false)
    private String name; // Containment Storage Facility, Cash/Bank, Chemical Supplier Payables, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type; // ASSET, LIABILITY, INCOME, EXPENSE, EQUITY

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}
