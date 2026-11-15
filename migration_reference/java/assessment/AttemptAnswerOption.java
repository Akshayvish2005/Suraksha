package com.suraksha.suraksha.assessment;

import jakarta.persistence.*;

// Maps to the attempt_answer_option table
@Entity
@Table(name = "attempt_answer_option")
@IdClass(AttemptAnswerOptionId.class)
public class AttemptAnswerOption {

    @Id
    @Column(name = "attempt_answer_id", nullable = false)
    private Long attemptAnswerId;

    @Id
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    protected AttemptAnswerOption() {
    }

    public AttemptAnswerOption(Long attemptAnswerId, Long optionId) {
        this.attemptAnswerId = attemptAnswerId;
        this.optionId = optionId;
    }

    public Long getAttemptAnswerId() { return attemptAnswerId; }
    public void setAttemptAnswerId(Long attemptAnswerId) { this.attemptAnswerId = attemptAnswerId; }
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
}
