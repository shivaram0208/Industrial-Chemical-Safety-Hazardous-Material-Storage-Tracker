package com.hazmat.tracker.entity;

import com.hazmat.tracker.entity.Enums.ContactType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contacts")
@Getter
@Setter
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType type; // CUSTOMER = Chemical Manufacturer, VENDOR = Neutralization Agent Vendor

    private String email;
    private String phone;
    private String address;
    private String gstOrTaxId;
}
