package com.suraksha.suraksha.web;

import com.suraksha.suraksha.assessment.AssessmentAttempt;
import com.suraksha.suraksha.assessment.AssessmentAttemptRepository;
import com.suraksha.suraksha.learning.LearningModule;
import com.suraksha.suraksha.learning.LearningModuleRepository;
import com.suraksha.suraksha.learning.ModuleSummary;
import com.suraksha.suraksha.progress.LessonProgressRepository;
import com.suraksha.suraksha.progress.ModuleProgress;
import com.suraksha.suraksha.progress.ModuleProgressRepository;
import com.suraksha.suraksha.scenario.ScenarioAttempt;
import com.suraksha.suraksha.scenario.ScenarioAttemptRepository;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ProgressController {

    public record ProgressStats(
            int modulesCompleted,
            int modulesTotal,
            int overallCompletionPct,
            Integer avgAssessmentScore,
            int certCount) {
    }

    public record ModuleProgressItem(
            String hazardCode,
            String title,
            Integer preTestScore,
            long totalLessons,
            long lessonsCompleted,
            int pct,
            Integer postTestScore,
            boolean certified) {
    }

    public record RecentAttemptItem(
            String moduleTitle,
            String attemptType,
            BigDecimal scorePercent,
            boolean passed,
            LocalDateTime submittedAt) {
    }

    public record BadgeItem(
            String name,
            String description,
            String iconUrl,
            LocalDateTime earnedAt) {
    }

    private final AppUserRepository appUserRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final ScenarioAttemptRepository scenarioAttemptRepository;

    public ProgressController(AppUserRepository appUserRepository,
            LearningModuleRepository learningModuleRepository,
            ModuleProgressRepository moduleProgressRepository,
            LessonProgressRepository lessonProgressRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            ScenarioAttemptRepository scenarioAttemptRepository) {
        this.appUserRepository = appUserRepository;
        this.learningModuleRepository = learningModuleRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.scenarioAttemptRepository = scenarioAttemptRepository;
    }

    @GetMapping("/progress")
    public String showProgress(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        List<ModuleSummary> modules = learningModuleRepository.findPublishedModulesEnglish();
        Map<Long, String> moduleTitleMap = new HashMap<>();
        for (ModuleSummary m : modules) {
            moduleTitleMap.put(m.getId(), m.getTitle());
        }

        List<ModuleProgressItem> progressList = new ArrayList<>();
        int modulesCompletedCount = 0;
        int certificatesCount = 0;
        long totalLessonsOverall = 0;
        long completedLessonsOverall = 0;

        for (ModuleSummary m : modules) {
            long totalLessons = moduleProgressRepository.countPublishedLessonsInModule(m.getId());
            long completedLessons = moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(),
                    m.getId());

            totalLessonsOverall += totalLessons;
            completedLessonsOverall += completedLessons;

            int pct = (totalLessons > 0)
                    ? (int) Math.min(100, Math.round((completedLessons * 100.0) / totalLessons))
                    : 0;

            Optional<AssessmentAttempt> preOpt = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), m.getId(), "PRE", "SUBMITTED");
            Integer preScore = preOpt.map(a -> a.getScorePercent() != null ? a.getScorePercent().intValue() : 0)
                    .orElse(null);

            Optional<AssessmentAttempt> postOpt = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), m.getId(), "POST", "SUBMITTED");
            Integer postScore = postOpt.map(a -> a.getScorePercent() != null ? a.getScorePercent().intValue() : 0)
                    .orElse(null);

            boolean passedPost = (postScore != null && postScore >= 60);
            if (passedPost) {
                certificatesCount++;
            }
            if (pct >= 100 || passedPost) {
                modulesCompletedCount++;
            }

            progressList.add(new ModuleProgressItem(
                    m.getHazardCode(),
                    m.getTitle(),
                    preScore,
                    totalLessons,
                    completedLessons,
                    pct,
                    postScore,
                    passedPost));
        }

        int overallCompletionPct = totalLessonsOverall > 0
                ? (int) Math.min(100, Math.round((completedLessonsOverall * 100.0) / totalLessonsOverall))
                : 0;

        List<AssessmentAttempt> submittedAttempts = assessmentAttemptRepository
                .findByUserIdAndStatusOrderBySubmittedAtDesc(user.getId(), "SUBMITTED");

        Integer avgAssessmentScore = null;
        if (!submittedAttempts.isEmpty()) {
            double sum = 0;
            int count = 0;
            for (AssessmentAttempt a : submittedAttempts) {
                if (a.getScorePercent() != null) {
                    sum += a.getScorePercent().doubleValue();
                    count++;
                }
            }
            if (count > 0) {
                avgAssessmentScore = (int) Math.round(sum / count);
            }
        }

        ProgressStats stats = new ProgressStats(
                modulesCompletedCount,
                modules.size(),
                overallCompletionPct,
                avgAssessmentScore,
                certificatesCount);

        List<RecentAttemptItem> recentAttempts = new ArrayList<>();
        for (AssessmentAttempt att : submittedAttempts) {
            String title = moduleTitleMap.getOrDefault(att.getModuleId(), "Disaster Safety Module");
            boolean passed = att.getScorePercent() != null
                    && att.getScorePercent().compareTo(BigDecimal.valueOf(60)) >= 0;
            recentAttempts.add(new RecentAttemptItem(
                    title,
                    att.getAttemptType(),
                    att.getScorePercent() != null ? att.getScorePercent().setScale(0, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                    passed,
                    att.getSubmittedAt() != null ? att.getSubmittedAt() : att.getStartedAt()));
        }

        // Compute Safety Badges based on genuine user activity
        List<BadgeItem> earnedBadges = new ArrayList<>();
        LocalDateTime baseTime = user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now();

        if (completedLessonsOverall > 0) {
            earnedBadges.add(new BadgeItem(
                    "First Responder",
                    "Completed your first disaster safety lesson.",
                    "⭐",
                    baseTime.plusHours(1)));
        }

        for (ModuleProgressItem item : progressList) {
            if (item.pct() >= 100 || item.certified()) {
                String badgeName = switch (item.hazardCode() != null ? item.hazardCode().toUpperCase() : "") {
                    case "EARTHQUAKE" -> "Earthquake Guardian";
                    case "FLOOD" -> "Flood Resilient";
                    case "FIRE" -> "Fire Marshal Specialist";
                    default -> "Safety Achiever";
                };
                String badgeIcon = switch (item.hazardCode() != null ? item.hazardCode().toUpperCase() : "") {
                    case "EARTHQUAKE" -> "🏔️";
                    case "FLOOD" -> "🌊";
                    case "FIRE" -> "🔥";
                    default -> "🛡️";
                };
                earnedBadges.add(new BadgeItem(
                        badgeName,
                        "Mastered all core curriculum lessons in " + item.title() + ".",
                        badgeIcon,
                        baseTime.plusDays(1)));
            }
        }

        if (certificatesCount >= 3 || (modules.size() > 0 && modulesCompletedCount >= modules.size())) {
            earnedBadges.add(new BadgeItem(
                    "Suraksha Master of Preparedness",
                    "Completed all primary hazard modules and post-training certifications.",
                    "🏆",
                    LocalDateTime.now()));
        }

        model.addAttribute("stats", stats);
        model.addAttribute("moduleProgressList", progressList);
        model.addAttribute("recentAttempts", recentAttempts);
        model.addAttribute("earnedBadges", earnedBadges);

        return "progress";
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
