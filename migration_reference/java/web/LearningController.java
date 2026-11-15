package com.suraksha.suraksha.web;

import com.suraksha.suraksha.assessment.AssessmentAttemptRepository;
import com.suraksha.suraksha.learning.*;
import com.suraksha.suraksha.progress.LessonProgress;
import com.suraksha.suraksha.progress.LessonProgressRepository;
import com.suraksha.suraksha.progress.ModuleProgress;
import com.suraksha.suraksha.progress.ModuleProgressRepository;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class LearningController {

    private final AppUserRepository appUserRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final LessonRepository lessonRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;

    public LearningController(AppUserRepository appUserRepository,
            LearningModuleRepository learningModuleRepository,
            LessonRepository lessonRepository,
            ModuleProgressRepository moduleProgressRepository,
            LessonProgressRepository lessonProgressRepository,
            AssessmentAttemptRepository assessmentAttemptRepository) {
        this.appUserRepository = appUserRepository;
        this.learningModuleRepository = learningModuleRepository;
        this.lessonRepository = lessonRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
    }

    @GetMapping("/modules")
    public String listModules(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        List<ModuleSummary> modules = learningModuleRepository.findPublishedModulesEnglish();

        populateUserModel(user, model);
        model.addAttribute("modules", modules);
        return "modules";
    }

    @GetMapping("/modules/{moduleId}")
    public String moduleDetail(@PathVariable("moduleId") Long moduleId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        ModuleSummary module = learningModuleRepository.findModuleById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        List<LessonListItem> lessons = lessonRepository.findLessonsForModule(moduleId);
        List<LessonListItem> lessonsBefore = lessons.stream()
                .filter(l -> "BEFORE".equalsIgnoreCase(l.getPhase()))
                .toList();
        List<LessonListItem> lessonsDuring = lessons.stream()
                .filter(l -> "DURING".equalsIgnoreCase(l.getPhase()))
                .toList();
        List<LessonListItem> lessonsAfter = lessons.stream()
                .filter(l -> "AFTER".equalsIgnoreCase(l.getPhase()))
                .toList();

        boolean preTestDone = assessmentAttemptRepository.existsByUserIdAndModuleIdAndAttemptTypeAndStatus(
                user.getId(), moduleId, "PRE", "SUBMITTED");
        boolean postTestDone = assessmentAttemptRepository.existsByUserIdAndModuleIdAndAttemptTypeAndStatus(
                user.getId(), moduleId, "POST", "SUBMITTED");

        long completedCount = moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(), moduleId);
        long totalCount = moduleProgressRepository.countPublishedLessonsInModule(moduleId);
        boolean allLessonsDone = (totalCount > 0 && completedCount >= totalCount);

        Set<Long> completedLessonIds = new HashSet<>(
                moduleProgressRepository.findCompletedLessonIdsForUserAndModule(user.getId(), moduleId));

        populateUserModel(user, model);
        model.addAttribute("module", module);
        model.addAttribute("lessonsBefore", lessonsBefore);
        model.addAttribute("lessonsDuring", lessonsDuring);
        model.addAttribute("lessonsAfter", lessonsAfter);
        model.addAttribute("preTestDone", preTestDone);
        model.addAttribute("allLessonsDone", allLessonsDone);
        model.addAttribute("postTestDone", postTestDone);
        model.addAttribute("completedLessonIds", completedLessonIds);

        return "module-detail";
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public String lessonDetail(@PathVariable("moduleId") Long moduleId,
            @PathVariable("lessonId") Long lessonId,
            @RequestParam(value = "completed", required = false) Boolean completed,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        ModuleSummary module = learningModuleRepository.findModuleById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        LessonDetail lesson = lessonRepository.findLessonDetail(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        List<LessonListItem> allLessons = lessonRepository.findLessonsForModule(moduleId);

        LessonListItem prevLesson = null;
        LessonListItem nextLesson = null;
        for (int i = 0; i < allLessons.size(); i++) {
            if (allLessons.get(i).getId().equals(lessonId)) {
                if (i > 0) {
                    prevLesson = allLessons.get(i - 1);
                }
                if (i < allLessons.size() - 1) {
                    nextLesson = allLessons.get(i + 1);
                }
                break;
            }
        }

        boolean lessonCompleted = lessonProgressRepository.existsByUserIdAndLessonId(user.getId(), lessonId);

        populateUserModel(user, model);
        model.addAttribute("module", module);
        model.addAttribute("lesson", lesson);
        model.addAttribute("allLessons", allLessons);
        model.addAttribute("lessonCompleted", lessonCompleted);
        model.addAttribute("prevLesson", prevLesson);
        model.addAttribute("nextLesson", nextLesson);
        if (Boolean.TRUE.equals(completed)) {
            model.addAttribute("completedJustNow", true);
        }

        return "lesson";
    }

    @PostMapping("/modules/{moduleId}/lessons/{lessonId}/complete")
    public String completeLesson(@PathVariable("moduleId") Long moduleId,
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);

        if (!lessonProgressRepository.existsByUserIdAndLessonId(user.getId(), lessonId)) {
            lessonProgressRepository.save(new LessonProgress(user.getId(), lessonId, LocalDateTime.now()));
        }

        ModuleProgress progress = getOrCreateModuleProgress(user.getId(), moduleId);
        if ("NOT_STARTED".equals(progress.getStatus())) {
            progress.setStatus("IN_PROGRESS");
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
        }

        long completedCount = moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(), moduleId);
        long totalCount = moduleProgressRepository.countPublishedLessonsInModule(moduleId);
        if (totalCount > 0 && completedCount >= totalCount) {
            progress.setStatus("COMPLETED");
            progress.setCompletedAt(LocalDateTime.now());
        }
        moduleProgressRepository.save(progress);

        return "redirect:/modules/" + moduleId + "/lessons/" + lessonId + "?completed=true";
    }

    private ModuleProgress getOrCreateModuleProgress(Long userId, Long moduleId) {
        return moduleProgressRepository.findByUserIdAndModuleId(userId, moduleId)
                .orElseGet(() -> {
                    ModuleProgress newProgress = new ModuleProgress(userId, moduleId, "NOT_STARTED");
                    return moduleProgressRepository.save(newProgress);
                });
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
