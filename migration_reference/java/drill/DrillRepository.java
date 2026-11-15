package com.suraksha.suraksha.drill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrillRepository extends JpaRepository<Drill, Long> {

        @Query(value = "SELECT d.id AS id, d.title AS title, d.hazard_id AS hazardId, " +
                        "h.name_en AS hazardName, h.code AS hazardCode, " +
                        "d.scenario_id AS scenarioId, s.title AS scenarioTitle, " +
                        "d.start_time AS startTime, d.end_time AS endTime, " +
                        "d.mode AS mode, d.campus_wide AS campusWide, d.status AS status, " +
                        "(SELECT COUNT(*) FROM drill_participant dp WHERE dp.drill_id = d.id) AS participantCount, " +
                        "(SELECT COUNT(*) FROM drill_participant dp WHERE dp.drill_id = d.id AND dp.status = 'COMPLETED') AS completedCount "
                        +
                        "FROM drill d " +
                        "JOIN hazard h ON h.id = d.hazard_id " +
                        "JOIN scenario s ON s.id = d.scenario_id " +
                        "WHERE d.institution_id = :institutionId OR :institutionId IS NULL " +
                        "ORDER BY d.start_time DESC", nativeQuery = true)
        List<DrillSummary> findAllDrillSummaries(@Param("institutionId") Long institutionId);

        @Query(value = "SELECT d.id AS id, d.title AS title, d.hazard_id AS hazardId, " +
                        "h.name_en AS hazardName, h.code AS hazardCode, " +
                        "d.scenario_id AS scenarioId, s.title AS scenarioTitle, " +
                        "d.start_time AS startTime, d.end_time AS endTime, " +
                        "d.mode AS mode, d.campus_wide AS campusWide, d.status AS status, " +
                        "(SELECT COUNT(*) FROM drill_participant dp WHERE dp.drill_id = d.id) AS participantCount, " +
                        "(SELECT COUNT(*) FROM drill_participant dp WHERE dp.drill_id = d.id AND dp.status = 'COMPLETED') AS completedCount "
                        +
                        "FROM drill d " +
                        "JOIN hazard h ON h.id = d.hazard_id " +
                        "JOIN scenario s ON s.id = d.scenario_id " +
                        "WHERE d.id = :drillId", nativeQuery = true)
        Optional<DrillSummary> findDrillSummaryById(@Param("drillId") Long drillId);
}
