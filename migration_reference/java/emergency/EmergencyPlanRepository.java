package com.suraksha.suraksha.emergency;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmergencyPlanRepository extends JpaRepository<EmergencyPlan, Long> {
    Optional<EmergencyPlan> findByInstitutionId(Long institutionId);
}

