package com.hazmat.tracker.entity;

import com.hazmat.tracker.entity.Enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. Hazmat Storage Space, Neutralization Agent, Compliance Safety Inspection

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type; // SERVICE or GOODS

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private String unitOfMeasure; // e.g. "per sq.ft/month", "per litre", "per inspection"

    private String hazardClass; // optional: flammable, corrosive, toxic, reactive etc.
}
