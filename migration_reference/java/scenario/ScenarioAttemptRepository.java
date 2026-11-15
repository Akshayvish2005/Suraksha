package com.suraksha.suraksha.scenario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScenarioAttemptRepository extends JpaRepository<ScenarioAttempt, Long> {

    List<ScenarioAttempt> findByUserIdAndScenarioId(Long userId, Long scenarioId);
}
