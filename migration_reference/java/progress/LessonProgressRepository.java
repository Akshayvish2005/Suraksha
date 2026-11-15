package com.suraksha.suraksha.progress;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, LessonProgressId> {

    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);
}
