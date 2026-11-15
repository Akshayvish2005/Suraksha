package com.suraksha.suraksha.emergency;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChecklistStatusRepository extends JpaRepository<ChecklistStatus, ChecklistStatusId> {
    List<ChecklistStatus> findByIdInstitutionId(Long institutionId);
}

