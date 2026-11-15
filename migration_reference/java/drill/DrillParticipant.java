package com.suraksha.suraksha.drill;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drill_participant")
public class DrillParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drill_id", nullable = false)
    private Long drillId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "drill_role", nullable = false, length = 20)
    private String drillRole = "STUDENT";

    @Column(nullable = false, length = 20)
    private String status = "ASSIGNED";

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "scenario_attempt_id")
    private Long scenarioAttemptId;

    @Column(name = "evacuation_route_id")
    private Long evacuationRouteId;

    @Column(name = "evacuation_time_seconds")
    private Integer evacuationTimeSeconds;

    public DrillParticipant() {
    }

    public DrillParticipant(Long drillId, Long userId, String drillRole, String status) {
        this.drillId = drillId;
        this.userId = userId;
        this.drillRole = drillRole;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getDrillId() {
        return drillId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDrillRole() {
        return drillRole;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public Long getScenarioAttemptId() {
        return scenarioAttemptId;
    }

    public Long getEvacuationRouteId() {
        return evacuationRouteId;
    }

    public Integer getEvacuationTimeSeconds() {
        return evacuationTimeSeconds;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setScenarioAttemptId(Long scenarioAttemptId) {
        this.scenarioAttemptId = scenarioAttemptId;
    }

    public void setEvacuationRouteId(Long evacuationRouteId) {
        this.evacuationRouteId = evacuationRouteId;
    }

    public void setEvacuationTimeSeconds(Integer evacuationTimeSeconds) {
        this.evacuationTimeSeconds = evacuationTimeSeconds;
    }
}
