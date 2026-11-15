package com.suraksha.suraksha.scenario;

public interface ScenarioSummary {
    Long getId();

    String getTitle();

    String getDescription();

    String getCode();

    Integer getEstimatedMinutes();

    String getHazardCode();

    String getHazardName();
}
