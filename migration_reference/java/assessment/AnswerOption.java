package com.suraksha.suraksha.assessment;

import jakarta.persistence.*;

// Maps to the answer_option table
@Entity
@Table(name = "answer_option")
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    protected AnswerOption() {
    }

    public AnswerOption(Long questionId, int sortOrder, boolean isCorrect) {
        this.questionId = questionId;
        this.sortOrder = sortOrder;
        this.isCorrect = isCorrect;
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
