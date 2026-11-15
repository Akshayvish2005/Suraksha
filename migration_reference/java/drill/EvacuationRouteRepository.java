package com.suraksha.suraksha.drill;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvacuationRouteRepository extends JpaRepository<EvacuationRoute, Long> {

    List<EvacuationRoute> findByInstitutionIdOrderBySortOrder(Long institutionId);

    List<EvacuationRoute> findAllByOrderBySortOrder();
}
