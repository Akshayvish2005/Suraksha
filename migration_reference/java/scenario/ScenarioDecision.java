package com.suraksha.suraksha.scenario;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Maps to the scenario_decision table
@Entity
@Table(name = "scenario_decision")
public class ScenarioDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "step_id", nullable = false, length = 50)
    private String stepId;

    @Column(name = "option_id", length = 50)
    private String optionId;

    @Column(nullable = false)
    private int points = 0;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "timed_out", nullable = false)
    private boolean timedOut = false;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt = LocalDateTime.now();

    protected ScenarioDecision() {
    }

    public ScenarioDecision(Long attemptId, String stepId, String optionId, int points,
            boolean isCorrect, boolean timedOut, Integer responseTimeMs, LocalDateTime decidedAt) {
        this.attemptId = attemptId;
        this.stepId = stepId;
        this.optionId = optionId;
        this.points = points;
        this.isCorrect = isCorrect;
        this.timedOut = timedOut;
        this.responseTimeMs = responseTimeMs;
        this.decidedAt = decidedAt != null ? decidedAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}
