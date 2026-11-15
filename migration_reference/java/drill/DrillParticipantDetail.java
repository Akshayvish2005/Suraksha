package com.suraksha.suraksha.drill;

import java.time.LocalDateTime;

public interface DrillParticipantDetail {
    Long getId();

    Long getUserId();

    String getFullName();

    String getUsername();

    String getDrillRole();

    String getStatus();

    LocalDateTime getJoinedAt();

    LocalDateTime getCompletedAt();

    Integer getEvacuationTimeSeconds();

    String getEvacuationRouteName();

    String getAssemblyPoint();

    Integer getScore();
}
