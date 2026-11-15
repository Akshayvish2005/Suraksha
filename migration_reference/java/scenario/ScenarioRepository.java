package com.suraksha.suraksha.scenario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

        @Query(value = "SELECT s.id AS id, s.title AS title, s.description AS description, " +
                        "s.code AS code, s.estimated_minutes AS estimatedMinutes, " +
                        "h.code AS hazardCode, h.name_en AS hazardName " +
                        "FROM scenario s " +
                        "JOIN hazard h ON h.id = s.hazard_id " +
                        "WHERE h.code = :code AND s.status = 'PUBLISHED' " +
                        "ORDER BY s.id", nativeQuery = true)
        List<ScenarioSummary> findPublishedByHazardCode(@Param("code") String code);

        @Query(value = "SELECT s.id AS id, s.title AS title, s.description AS description, " +
                        "s.code AS code, s.estimated_minutes AS estimatedMinutes, " +
                        "h.code AS hazardCode, h.name_en AS hazardName " +
                        "FROM scenario s " +
                        "LEFT JOIN hazard h ON h.id = s.hazard_id " +
                        "WHERE s.status = 'PUBLISHED' " +
                        "ORDER BY s.hazard_id, s.id", nativeQuery = true)
        List<ScenarioSummary> findAllPublished();
}
