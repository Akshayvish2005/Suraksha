package com.suraksha.suraksha.drill;

import java.time.LocalDateTime;

public interface DrillSummary {
    Long getId();

    String getTitle();

    Long getHazardId();

    String getHazardName();

    String getHazardCode();

    Long getScenarioId();

    String getScenarioTitle();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    String getMode();

    Boolean getCampusWide();

    String getStatus();

    Long getParticipantCount();

    Long getCompletedCount();
}
