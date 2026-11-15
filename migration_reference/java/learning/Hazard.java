package com.suraksha.suraksha.learning;

import jakarta.persistence.*;

// Maps to the hazard table (already seeded with Earthquake, Flood, Fire)
@Entity
@Table(name = "hazard")
public class Hazard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_hi", length = 100)
    private String nameHi;

    @Column(nullable = false)
    private boolean active = true;

    protected Hazard() {
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameHi() {
        return nameHi;
    }

    public boolean isActive() {
        return active;
    }
}