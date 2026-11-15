package com.suraksha.suraksha.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {

    List<AssessmentAttempt> findByUserIdAndModuleIdAndAttemptType(Long userId, Long moduleId, String attemptType);

    Optional<AssessmentAttempt> findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
            Long userId, Long moduleId, String attemptType, String status);

    boolean existsByUserIdAndModuleIdAndAttemptTypeAndStatus(
            Long userId, Long moduleId, String attemptType, String status);

    List<AssessmentAttempt> findByUserIdOrderByStartedAtDesc(Long userId);

    List<AssessmentAttempt> findByUserIdAndStatusOrderBySubmittedAtDesc(Long userId, String status);

    long countByUserIdAndStatus(Long userId, String status);
}
