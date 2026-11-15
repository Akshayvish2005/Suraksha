package com.suraksha.suraksha.assessment;

public interface AnswerOptionView {
    Long getId();
    String getOptionText();
    Boolean getIsCorrect();
    Integer getSortOrder();
}
