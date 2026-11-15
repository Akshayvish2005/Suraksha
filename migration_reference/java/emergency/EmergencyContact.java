package com.suraksha.suraksha.emergency;

import jakarta.persistence.*;

@Entity
@Table(name = "emergency_contact")
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_id")
    private Long institutionId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 50)
    private String category;

    @Column(name = "is_national", nullable = false)
    private boolean isNational = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public EmergencyContact() {
    }

    public EmergencyContact(Long institutionId, String name, String phone, String category, boolean isNational, int sortOrder) {
        this.institutionId = institutionId;
        this.name = name;
        this.phone = phone;
        this.category = category;
        this.isNational = isNational;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public boolean isNational() {
        return isNational;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}

