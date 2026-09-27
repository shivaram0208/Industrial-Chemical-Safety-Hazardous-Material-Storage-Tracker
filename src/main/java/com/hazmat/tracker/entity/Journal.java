package com.hazmat.tracker.entity;

import com.hazmat.tracker.entity.Enums.JournalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "journals")
@Getter
@Setter
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Sales Journal", "Purchase Journal", "Cash Journal", "Bank Journal"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalType type;
}
