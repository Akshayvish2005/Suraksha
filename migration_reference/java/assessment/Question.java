package com.suraksha.suraksha.assessment;

import jakarta.persistence.*;

// Maps to the question table
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(nullable = false, length = 10)
    private String purpose = "BOTH";

    @Column(name = "q_type", nullable = false, length = 20)
    private String qType = "MCQ";

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    protected Question() {
    }

    public Question(Long moduleId, String purpose, String qType, int sortOrder, String status) {
        this.moduleId = moduleId;
        this.purpose = purpose;
        this.qType = qType;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getQType() { return qType; }
    public void setQType(String qType) { this.qType = qType; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
