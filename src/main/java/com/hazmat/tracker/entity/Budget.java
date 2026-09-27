package com.hazmat.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@Getter
@Setter
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "analytic_account_id", nullable = false)
    private AnalyticAccount analyticAccount;

    @Column(nullable = false)
    private String name; // e.g. "Sector 4 Safety Ops Budget - Q1"

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column(nullable = false)
    private BigDecimal allocatedAmount;

    @Column(nullable = false)
    private BigDecimal actualAmount = BigDecimal.ZERO; // updated as expenses booked against this sector

    @Transient
    public BigDecimal getVariance() {
        return allocatedAmount.subtract(actualAmount);
    }
}
