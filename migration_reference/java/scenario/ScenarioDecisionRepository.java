package com.suraksha.suraksha.scenario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScenarioDecisionRepository extends JpaRepository<ScenarioDecision, Long> {

    List<ScenarioDecision> findByAttemptIdOrderByDecidedAt(Long attemptId);

    Optional<ScenarioDecision> findByAttemptIdAndStepId(Long attemptId, String stepId);

    boolean existsByAttemptIdAndStepId(Long attemptId, String stepId);
}
