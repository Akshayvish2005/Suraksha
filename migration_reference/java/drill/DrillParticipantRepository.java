package com.suraksha.suraksha.drill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrillParticipantRepository extends JpaRepository<DrillParticipant, Long> {

    Optional<DrillParticipant> findByDrillIdAndUserId(Long drillId, Long userId);

    List<DrillParticipant> findByDrillId(Long drillId);

    long countByDrillId(Long drillId);

    long countByDrillIdAndStatus(Long drillId, String status);

    long countByUserIdAndStatus(Long userId, String status);

    @Query(value = "SELECT dp.id AS id, dp.user_id AS userId, u.full_name AS fullName, " +
            "u.username AS username, dp.drill_role AS drillRole, dp.status AS status, " +
            "dp.joined_at AS joinedAt, dp.completed_at AS completedAt, " +
            "dp.evacuation_time_seconds AS evacuationTimeSeconds, " +
            "er.name AS evacuationRouteName, er.assembly_point AS assemblyPoint, " +
            "sa.score AS score " +
            "FROM drill_participant dp " +
            "JOIN app_user u ON u.id = dp.user_id " +
            "LEFT JOIN evacuation_route er ON er.id = dp.evacuation_route_id " +
            "LEFT JOIN scenario_attempt sa ON sa.id = dp.scenario_attempt_id " +
            "WHERE dp.drill_id = :drillId " +
            "ORDER BY dp.status DESC, dp.completed_at ASC", nativeQuery = true)
    List<DrillParticipantDetail> findParticipantDetailsByDrillId(@Param("drillId") Long drillId);
}
