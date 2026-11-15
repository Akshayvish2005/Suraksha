package com.suraksha.suraksha.progress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {

    @Query(value = "SELECT * FROM module_progress WHERE user_id = :userId AND module_id = :moduleId", nativeQuery = true)
    Optional<ModuleProgress> findByUserIdAndModuleId(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    @Query(value = "SELECT COUNT(*) FROM lesson_progress lp JOIN lesson l ON l.id = lp.lesson_id WHERE lp.user_id = :userId AND l.module_id = :moduleId", nativeQuery = true)
    long countCompletedLessonsForUserModule(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    @Query(value = "SELECT COUNT(*) FROM lesson WHERE module_id = :moduleId AND status = 'PUBLISHED'", nativeQuery = true)
    long countPublishedLessonsInModule(@Param("moduleId") Long moduleId);

    @Query(value = "SELECT lp.lesson_id FROM lesson_progress lp JOIN lesson l ON l.id = lp.lesson_id WHERE lp.user_id = :userId AND l.module_id = :moduleId", nativeQuery = true)
    List<Long> findCompletedLessonIdsForUserAndModule(@Param("userId") Long userId, @Param("moduleId") Long moduleId);
}
