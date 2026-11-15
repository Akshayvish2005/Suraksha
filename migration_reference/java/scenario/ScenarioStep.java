package com.suraksha.suraksha.scenario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScenarioStep(
                String id,
                String narrative,
                Integer timerSeconds,
                List<ScenarioOption> options) {
}
