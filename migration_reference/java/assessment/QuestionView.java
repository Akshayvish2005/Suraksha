package com.suraksha.suraksha.assessment;

public interface QuestionView {
    Long getId();
    String getQuestionText();
    String getExplanation();
    String getQType();
    Integer getSortOrder();
}
