package com.suraksha.suraksha.learning;

import jakarta.persistence.*;

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false, length = 10)
    private String phase;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    protected Lesson() {
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getPhase() {
        return phase;
    }

    public String getStatus() {
        return status;
    }
}
