package com.suraksha.suraksha.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HazardRepository extends JpaRepository<Hazard, Long> {
    Optional<Hazard> findByCode(String code);
    List<Hazard> findByActiveTrue();
}

