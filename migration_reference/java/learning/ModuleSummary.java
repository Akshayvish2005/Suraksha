package com.suraksha.suraksha.learning;

// Describes the exact pieces of data we want for each module on the dashboard.
// Spring automatically fills this in from the database query results.
public interface ModuleSummary {
    Long getId();

    String getHazardCode();

    String getHazardName();

    String getTitle();

    String getDescription();
}