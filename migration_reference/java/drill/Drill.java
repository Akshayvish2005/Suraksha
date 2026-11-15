package com.suraksha.suraksha.drill;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drill")
public class Drill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "hazard_id", nullable = false)
    private Long hazardId;

    @Column(name = "scenario_id", nullable = false)
    private Long scenarioId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 20)
    private String mode = "SELF_PACED";

    @Column(name = "campus_wide", nullable = false)
    private boolean campusWide = false;

    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    public Drill() {
    }

    public Drill(Long institutionId, Long hazardId, Long scenarioId, String title, Long createdBy,
            LocalDateTime startTime, LocalDateTime endTime, String mode, boolean campusWide, String status) {
        this.institutionId = institutionId;
        this.hazardId = hazardId;
        this.scenarioId = scenarioId;
        this.title = title;
        this.createdBy = createdBy;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mode = mode;
        this.campusWide = campusWide;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public Long getHazardId() {
        return hazardId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public String getTitle() {
        return title;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getMode() {
        return mode;
    }

    public boolean isCampusWide() {
        return campusWide;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
