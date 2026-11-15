package com.suraksha.suraksha.web;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.suraksha.suraksha.scenario.*;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScenarioController {

    public record DecisionResultItem(
            String stepId,
            String stepNarrative,
            String optionId,
            String optionText,
            boolean isCorrect,
            int points,
            String feedback,
            boolean timedOut) {
    }

    private final AppUserRepository appUserRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioAttemptRepository scenarioAttemptRepository;
    private final ScenarioDecisionRepository scenarioDecisionRepository;
    private final ObjectMapper objectMapper;

    public ScenarioController(AppUserRepository appUserRepository,
            ScenarioRepository scenarioRepository,
            ScenarioAttemptRepository scenarioAttemptRepository,
            ScenarioDecisionRepository scenarioDecisionRepository,
            ObjectMapper objectMapper) {
        this.appUserRepository = appUserRepository;
        this.scenarioRepository = scenarioRepository;
        this.scenarioAttemptRepository = scenarioAttemptRepository;
        this.scenarioDecisionRepository = scenarioDecisionRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/simulations")
    public String listSimulations(@RequestParam(name = "hazard", required = false) String hazard,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        List<ScenarioSummary> scenarios;
        if (hazard != null && !hazard.isBlank() && !"all".equalsIgnoreCase(hazard)) {
            scenarios = scenarioRepository.findPublishedByHazardCode(hazard.toUpperCase());
        } else {
            scenarios = scenarioRepository.findAllPublished();
        }

        Map<String, List<ScenarioSummary>> scenariosByHazard = scenarios.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getHazardName() != null ? s.getHazardName() : "General",
                        LinkedHashMap::new,
                        Collectors.toList()));

        populateUserModel(user, model);
        model.addAttribute("scenarios", scenarios);
        model.addAttribute("scenariosByHazard", scenariosByHazard);
        model.addAttribute("activeHazardFilter", hazard != null ? hazard.toLowerCase() : "all");

        return "simulations";
    }

    @GetMapping("/simulations/{scenarioId}/start")
    public String startSimulation(@PathVariable("scenarioId") Long scenarioId,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenario not found"));

        ScenarioDef scenarioDef = parseScenarioDef(scenario.getDefinition());

        int maxScore = 0;
        if (scenarioDef.steps() != null) {
            for (ScenarioStep step : scenarioDef.steps()) {
                if (step.options() != null && !step.options().isEmpty()) {
                    int stepMax = step.options().stream()
                            .mapToInt(ScenarioOption::points)
                            .max()
                            .orElse(0);
                    maxScore += Math.max(0, stepMax);
                }
            }
        }

        ScenarioAttempt attempt = new ScenarioAttempt(
                user.getId(),
                scenarioId,
                null,
                LocalDateTime.now(),
                null,
                0,
                maxScore,
                null);
        attempt = scenarioAttemptRepository.save(attempt);

        String firstStepId = (scenarioDef.steps() != null && !scenarioDef.steps().isEmpty())
                ? scenarioDef.steps().get(0).id()
                : "1";

        return "redirect:/simulations/" + scenarioId + "/step/" + firstStepId + "?attemptId=" + attempt.getId();
    }

    @GetMapping("/simulations/{scenarioId}/step/{stepId}")
    public String showStep(@PathVariable("scenarioId") Long scenarioId,
            @PathVariable("stepId") String stepId,
            @RequestParam("attemptId") Long attemptId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenario not found"));

        ScenarioAttempt attempt = scenarioAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        ScenarioDef scenarioDef = parseScenarioDef(scenario.getDefinition());

        ScenarioStep step = scenarioDef.steps() != null
                ? scenarioDef.steps().stream().filter(s -> s.id().equals(stepId)).findFirst().orElse(null)
                : null;

        if (step == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Step not found");
        }

        Optional<ScenarioDecision> decisionOpt = scenarioDecisionRepository.findByAttemptIdAndStepId(attemptId, stepId);
        boolean alreadyDecided = decisionOpt.isPresent();
        ScenarioDecision decision = decisionOpt.orElse(null);

        int totalSteps = scenarioDef.steps() != null ? scenarioDef.steps().size() : 0;
        int stepNumber = 1;
        if (scenarioDef.steps() != null) {
            for (int i = 0; i < scenarioDef.steps().size(); i++) {
                if (scenarioDef.steps().get(i).id().equals(stepId)) {
                    stepNumber = i + 1;
                    break;
                }
            }
        }

        populateUserModel(user, model);
        model.addAttribute("scenario", scenario);
        model.addAttribute("step", step);
        model.addAttribute("options", step.options() != null ? step.options() : Collections.emptyList());
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("alreadyDecided", alreadyDecided);
        model.addAttribute("decision", decision);
        model.addAttribute("totalSteps", totalSteps);
        model.addAttribute("stepNumber", stepNumber);

        return "scenario";
    }

    @PostMapping("/simulations/{scenarioId}/step/{stepId}")
    public String submitDecision(@PathVariable("scenarioId") Long scenarioId,
            @PathVariable("stepId") String stepId,
            @RequestParam("attemptId") Long attemptId,
            @RequestParam(name = "optionId", required = false) String optionId,
            @RequestParam(name = "responseTimeMs", required = false) Integer responseTimeMs,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenario not found"));

        ScenarioAttempt attempt = scenarioAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        ScenarioDef scenarioDef = parseScenarioDef(scenario.getDefinition());

        ScenarioStep step = scenarioDef.steps() != null
                ? scenarioDef.steps().stream().filter(s -> s.id().equals(stepId)).findFirst().orElse(null)
                : null;

        if (step == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Step not found");
        }

        ScenarioOption chosenOption = (step.options() != null && optionId != null)
                ? step.options().stream().filter(o -> o.id().equals(optionId)).findFirst().orElse(null)
                : null;

        boolean timedOut = (optionId == null || optionId.isBlank());
        int points = chosenOption != null ? chosenOption.points() : 0;
        boolean isCorrect = chosenOption != null && chosenOption.correct();

        Optional<ScenarioDecision> existingDecision = scenarioDecisionRepository.findByAttemptIdAndStepId(attemptId,
                stepId);
        if (existingDecision.isEmpty()) {
            ScenarioDecision decision = new ScenarioDecision(
                    attemptId,
                    stepId,
                    optionId,
                    points,
                    isCorrect,
                    timedOut,
                    responseTimeMs,
                    LocalDateTime.now());
            scenarioDecisionRepository.save(decision);
        }

        String nextStepId = chosenOption != null ? chosenOption.nextStepId() : null;
        boolean nextStepExists = false;
        if (nextStepId != null && !nextStepId.isBlank() && scenarioDef.steps() != null) {
            nextStepExists = scenarioDef.steps().stream().anyMatch(s -> s.id().equals(nextStepId));
        }

        if (nextStepExists) {
            return "redirect:/simulations/" + scenarioId + "/step/" + nextStepId + "?attemptId=" + attemptId;
        } else {
            List<ScenarioDecision> decisions = scenarioDecisionRepository.findByAttemptIdOrderByDecidedAt(attemptId);
            int totalPoints = decisions.stream().mapToInt(ScenarioDecision::getPoints).sum();
            LocalDateTime now = LocalDateTime.now();
            long durationSec = (attempt.getStartedAt() != null)
                    ? Duration.between(attempt.getStartedAt(), now).getSeconds()
                    : 0;

            attempt.setScore(totalPoints);
            attempt.setCompletedAt(now);
            attempt.setTotalTimeSeconds((int) durationSec);
            scenarioAttemptRepository.save(attempt);

            return "redirect:/simulations/attempt/" + attemptId + "/result";
        }
    }

    @GetMapping("/simulations/attempt/{attemptId}/result")
    public String showResult(@PathVariable("attemptId") Long attemptId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        ScenarioAttempt attempt = scenarioAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        Scenario scenario = scenarioRepository.findById(attempt.getScenarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenario not found"));

        ScenarioDef scenarioDef = parseScenarioDef(scenario.getDefinition());
        List<ScenarioDecision> decisions = scenarioDecisionRepository.findByAttemptIdOrderByDecidedAt(attemptId);

        List<DecisionResultItem> decisionResults = new ArrayList<>();
        Map<String, ScenarioStep> stepMap = (scenarioDef.steps() != null)
                ? scenarioDef.steps().stream().collect(Collectors.toMap(ScenarioStep::id, s -> s, (s1, s2) -> s1))
                : Collections.emptyMap();

        for (ScenarioDecision dec : decisions) {
            ScenarioStep step = stepMap.get(dec.getStepId());
            String stepNarrative = step != null ? step.narrative() : "";
            ScenarioOption opt = (step != null && step.options() != null && dec.getOptionId() != null)
                    ? step.options().stream().filter(o -> o.id().equals(dec.getOptionId())).findFirst().orElse(null)
                    : null;

            String optionText = opt != null ? opt.text() : (dec.isTimedOut() ? "Time expired" : "No option selected");
            String feedback = opt != null ? opt.feedback() : "";

            decisionResults.add(new DecisionResultItem(
                    dec.getStepId(),
                    stepNarrative,
                    dec.getOptionId(),
                    optionText,
                    dec.isCorrect(),
                    dec.getPoints(),
                    feedback,
                    dec.isTimedOut()));
        }

        int totalScore = attempt.getScore() != null ? attempt.getScore() : 0;
        int maxScore = attempt.getMaxScore() != null ? attempt.getMaxScore() : 0;
        int pct = maxScore > 0 ? (int) Math.round((double) totalScore * 100 / maxScore) : 0;

        populateUserModel(user, model);
        model.addAttribute("attempt", attempt);
        model.addAttribute("scenario", scenario);
        model.addAttribute("decisions", decisionResults);
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("maxScore", maxScore);
        model.addAttribute("pct", pct);

        return "scenario-result";
    }

    private ScenarioDef parseScenarioDef(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new ScenarioDef(Collections.emptyList());
            }
            return objectMapper.readValue(json, ScenarioDef.class);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to parse scenario definition JSON", e);
        }
    }

    private AppUser getUser(UserDetails principal) {
        return appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void populateUserModel(AppUser user, Model model) {
        String[] nameParts = user.getFullName().trim().split("\\s+");
        String initials = nameParts.length >= 2
                ? String.valueOf(nameParts[0].charAt(0)) + String.valueOf(nameParts[nameParts.length - 1].charAt(0))
                : String.valueOf(nameParts[0].charAt(0));

        String roleLabel = switch (user.getRole()) {
            case STUDENT -> "Student";
            case TEACHER -> "Teacher";
            case INSTITUTION_ADMIN -> "Institution Admin";
            case SYSTEM_ADMIN -> "System Admin";
        };

        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("role", user.getRole());
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("initials", initials.toUpperCase());
    }
}
