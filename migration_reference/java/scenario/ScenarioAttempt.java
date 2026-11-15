package com.suraksha.suraksha.scenario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Maps to the scenario_attempt table
@Entity
@Table(name = "scenario_attempt")
public class ScenarioAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "scenario_id", nullable = false)
    private Long scenarioId;

    @Column(name = "drill_id")
    private Long drillId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column
    private Integer score = 0;

    @Column(name = "max_score")
    private Integer maxScore = 0;

    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    protected ScenarioAttempt() {
    }

    public ScenarioAttempt(Long userId, Long scenarioId, Long drillId, LocalDateTime startedAt,
            LocalDateTime completedAt, Integer score, Integer maxScore, Integer totalTimeSeconds) {
        this.userId = userId;
        this.scenarioId = scenarioId;
        this.drillId = drillId;
        this.startedAt = startedAt != null ? startedAt : LocalDateTime.now();
        this.completedAt = completedAt;
        this.score = score != null ? score : 0;
        this.maxScore = maxScore != null ? maxScore : 0;
        this.totalTimeSeconds = totalTimeSeconds;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getDrillId() {
        return drillId;
    }

    public void setDrillId(Long drillId) {
        this.drillId = drillId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public void setTotalTimeSeconds(Integer totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }
}
