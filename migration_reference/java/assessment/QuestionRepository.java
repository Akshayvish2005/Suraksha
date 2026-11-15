package com.suraksha.suraksha.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT q.id AS id, qt.question_text AS questionText, qt.explanation AS explanation, " +
            "q.q_type AS qType, q.sort_order AS sortOrder " +
            "FROM question q " +
            "JOIN question_text qt ON qt.question_id = q.id AND qt.language = 'en' " +
            "WHERE q.module_id = :moduleId AND q.status = 'PUBLISHED' AND (q.purpose = :type OR q.purpose = 'BOTH') " +
            "ORDER BY q.sort_order", nativeQuery = true)
    List<QuestionView> findPublishedQuestionsForModule(@Param("moduleId") Long moduleId, @Param("type") String type);

    @Query(value = "SELECT ao.id AS id, aot.option_text AS optionText, ao.is_correct AS isCorrect, ao.sort_order AS sortOrder " +
            "FROM answer_option ao " +
            "JOIN answer_option_text aot ON aot.option_id = ao.id AND aot.language = 'en' " +
            "WHERE ao.question_id = :questionId ORDER BY ao.sort_order", nativeQuery = true)
    List<AnswerOptionView> findOptionsForQuestion(@Param("questionId") Long questionId);

    @Query(value = "SELECT q.id AS id, qt.question_text AS questionText, qt.explanation AS explanation, " +
            "q.q_type AS qType, q.sort_order AS sortOrder " +
            "FROM question q " +
            "JOIN question_text qt ON qt.question_id = q.id AND qt.language = 'en' " +
            "WHERE q.id = :questionId", nativeQuery = true)
    Optional<QuestionView> findQuestionViewById(@Param("questionId") Long questionId);
}
