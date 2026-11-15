package com.suraksha.suraksha.assessment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Maps to the assessment_attempt table
@Entity
@Table(name = "assessment_attempt")
public class AssessmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "attempt_type", nullable = false, length = 10)
    private String attemptType;

    @Column(nullable = false, length = 20)
    private String status = "IN_PROGRESS";

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "score_percent", precision = 5, scale = 2)
    private BigDecimal scorePercent;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    protected AssessmentAttempt() {
    }

    public AssessmentAttempt(Long userId, Long moduleId, String attemptType, String status, LocalDateTime startedAt) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.attemptType = attemptType;
        this.status = status;
        this.startedAt = startedAt != null ? startedAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public String getAttemptType() { return attemptType; }
    public void setAttemptType(String attemptType) { this.attemptType = attemptType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
    public BigDecimal getScorePercent() { return scorePercent; }
    public void setScorePercent(BigDecimal scorePercent) { this.scorePercent = scorePercent; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
