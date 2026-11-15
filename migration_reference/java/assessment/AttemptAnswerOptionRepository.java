package com.suraksha.suraksha.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttemptAnswerOptionRepository extends JpaRepository<AttemptAnswerOption, AttemptAnswerOptionId> {

    List<AttemptAnswerOption> findByAttemptAnswerId(Long attemptAnswerId);
}
