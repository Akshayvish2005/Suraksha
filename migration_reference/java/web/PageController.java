package com.suraksha.suraksha.web;

import com.suraksha.suraksha.assessment.AssessmentAttemptRepository;
import com.suraksha.suraksha.drill.DrillParticipantRepository;
import com.suraksha.suraksha.learning.LearningModuleRepository;
import com.suraksha.suraksha.learning.ModuleSummary;
import com.suraksha.suraksha.progress.ModuleProgressRepository;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PageController {

    private final AppUserRepository appUserRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final DrillParticipantRepository drillParticipantRepository;

    public PageController(AppUserRepository appUserRepository,
                          LearningModuleRepository learningModuleRepository,
                          ModuleProgressRepository moduleProgressRepository,
                          AssessmentAttemptRepository assessmentAttemptRepository,
                          DrillParticipantRepository drillParticipantRepository) {
        this.appUserRepository = appUserRepository;
        this.learningModuleRepository = learningModuleRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.drillParticipantRepository = drillParticipantRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession(true);
        return "login";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername()).orElseThrow();
        populateUser(user, model);

        List<ModuleSummary> modules = learningModuleRepository.findPublishedModulesEnglish();

        long totalLessons = 0;
        long completedLessons = 0;
        for (ModuleSummary m : modules) {
            totalLessons += moduleProgressRepository.countPublishedLessonsInModule(m.getId());
            completedLessons += moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(), m.getId());
        }
        int overallProgress = totalLessons > 0
                ? (int) Math.min(100, Math.round((completedLessons * 100.0) / totalLessons))
                : 0;

        model.addAttribute("modules", modules);
        model.addAttribute("modulesCount", modules.size());
        model.addAttribute("overallProgress", overallProgress);

        return "home";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername()).orElseThrow();
        populateUser(user, model);

        List<ModuleSummary> modules = learningModuleRepository.findPublishedModulesEnglish();
        long completedLessons = 0;
        long totalLessons = 0;
        for (ModuleSummary m : modules) {
            totalLessons += moduleProgressRepository.countPublishedLessonsInModule(m.getId());
            completedLessons += moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(), m.getId());
        }

        long completedDrills = drillParticipantRepository.countByUserIdAndStatus(user.getId(), "COMPLETED");
        long completedAssessments = assessmentAttemptRepository.countByUserIdAndStatus(user.getId(), "SUBMITTED");

        model.addAttribute("email", user.getEmail() != null ? user.getEmail() : "N/A");
        model.addAttribute("language", user.getLanguage() != null ? user.getLanguage().toUpperCase() : "EN");
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedDrills", completedDrills);
        model.addAttribute("completedAssessments", completedAssessments);
        model.addAttribute("institutionName", "Delhi Public School Bhopal");

        return "profile";
    }

    @PostMapping("/profile/language")
    public String updateLanguage(@RequestParam("lang") String lang,
                                 @AuthenticationPrincipal UserDetails principal) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername()).orElseThrow();
        user.setLanguage(lang.toLowerCase());
        appUserRepository.save(user);
        return "redirect:/profile?saved=true";
    }

    @GetMapping("/help")
    public String help(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername()).orElseThrow();
        populateUser(user, model);
        return "help";
    }

    private void populateUser(AppUser user, Model model) {
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
        model.addAttribute("userName", user.getFullName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("initials", initials.toUpperCase());
        model.addAttribute("userInitials", initials.toUpperCase());
    }
}
