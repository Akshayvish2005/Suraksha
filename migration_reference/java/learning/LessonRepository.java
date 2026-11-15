package com.suraksha.suraksha.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

        @Query(value = "SELECT l.id AS id, lt.title AS title, l.phase AS phase, l.sort_order AS sortOrder " +
                        "FROM lesson l JOIN lesson_text lt ON lt.lesson_id = l.id AND lt.language = 'en' " +
                        "WHERE l.module_id = :moduleId AND l.status = 'PUBLISHED' ORDER BY l.sort_order", nativeQuery = true)
        List<LessonListItem> findLessonsForModule(@Param("moduleId") Long moduleId);

        @Query(value = "SELECT l.id AS id, l.module_id AS moduleId, lt.title AS title, lt.body AS body, l.phase AS phase "
                        +
                        "FROM lesson l JOIN lesson_text lt ON lt.lesson_id = l.id AND lt.language = 'en' " +
                        "WHERE l.id = :lessonId", nativeQuery = true)
        Optional<LessonDetail> findLessonDetail(@Param("lessonId") Long lessonId);
}