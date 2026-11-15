package com.suraksha.suraksha.assessment;

import java.io.Serializable;
import java.util.Objects;

public class AttemptAnswerOptionId implements Serializable {

    private Long attemptAnswerId;
    private Long optionId;

    public AttemptAnswerOptionId() {
    }

    public AttemptAnswerOptionId(Long attemptAnswerId, Long optionId) {
        this.attemptAnswerId = attemptAnswerId;
        this.optionId = optionId;
    }

    public Long getAttemptAnswerId() { return attemptAnswerId; }
    public void setAttemptAnswerId(Long attemptAnswerId) { this.attemptAnswerId = attemptAnswerId; }
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttemptAnswerOptionId that = (AttemptAnswerOptionId) o;
        return Objects.equals(attemptAnswerId, that.attemptAnswerId) && Objects.equals(optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptAnswerId, optionId);
    }
}
