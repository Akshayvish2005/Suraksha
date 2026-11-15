package com.suraksha.suraksha.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScenarioOption(
                String id,
                String text,
                String nextStepId,
                boolean correct,
                int points,
                String feedback) {
}
