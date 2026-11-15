package com.suraksha.suraksha.assessment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Maps to the attempt_answer table
@Entity
@Table(name = "attempt_answer")
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt = LocalDateTime.now();

    protected AttemptAnswer() {
    }

    public AttemptAnswer(Long attemptId, Long questionId, boolean isCorrect, LocalDateTime answeredAt) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.isCorrect = isCorrect;
        this.answeredAt = answeredAt != null ? answeredAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}
