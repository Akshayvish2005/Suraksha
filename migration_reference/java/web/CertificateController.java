package com.suraksha.suraksha.web;

import com.suraksha.suraksha.assessment.AssessmentAttempt;
import com.suraksha.suraksha.assessment.AssessmentAttemptRepository;
import com.suraksha.suraksha.learning.LearningModule;
import com.suraksha.suraksha.learning.LearningModuleRepository;
import com.suraksha.suraksha.learning.ModuleSummary;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class CertificateController {

        public record CertificateItem(
                        Long moduleId,
                        String moduleTitle,
                        String hazardCode,
                        String hazardName,
                        boolean isUnlocked,
                        Integer scorePercent,
                        LocalDateTime issuedAt,
                        String certificateId) {
        }

        private final AppUserRepository appUserRepository;
        private final LearningModuleRepository learningModuleRepository;
        private final AssessmentAttemptRepository assessmentAttemptRepository;

        public CertificateController(AppUserRepository appUserRepository,
                        LearningModuleRepository learningModuleRepository,
                        AssessmentAttemptRepository assessmentAttemptRepository) {
                this.appUserRepository = appUserRepository;
                this.learningModuleRepository = learningModuleRepository;
                this.assessmentAttemptRepository = assessmentAttemptRepository;
        }

        @GetMapping("/certificates")
        public String listCertificates(@AuthenticationPrincipal UserDetails principal, Model model) {
                AppUser user = getUser(principal);
                populateUserModel(user, model);

                List<ModuleSummary> modules = learningModuleRepository.findPublishedModulesEnglish();
                List<CertificateItem> certificates = new ArrayList<>();

                for (ModuleSummary m : modules) {
                        LearningModule lm = learningModuleRepository.findById(m.getId()).orElse(null);
                        int passMark = lm != null ? lm.getPassMarkPercent() : 60;

                        Optional<AssessmentAttempt> postOpt = assessmentAttemptRepository
                                        .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                                                        user.getId(), m.getId(), "POST", "SUBMITTED");

                        boolean passed = postOpt.isPresent()
                                        && postOpt.get().getScorePercent() != null
                                        && postOpt.get().getScorePercent().intValue() >= passMark;

                        Integer score = postOpt
                                        .map(a -> a.getScorePercent() != null ? a.getScorePercent().intValue() : 0)
                                        .orElse(null);
                        LocalDateTime date = postOpt.map(AssessmentAttempt::getSubmittedAt).orElse(null);
                        String certId = "SURAKSHA-" + m.getHazardCode() + "-" + String.format("%04d", user.getId());

                        certificates.add(new CertificateItem(
                                        m.getId(),
                                        m.getTitle(),
                                        m.getHazardCode(),
                                        m.getHazardName(),
                                        passed,
                                        score,
                                        date,
                                        certId));
                }

                model.addAttribute("certificates", certificates);
                return "certificates";
        }

        @GetMapping("/certificates/{moduleId}/view")
        public String viewCertificate(@PathVariable("moduleId") Long moduleId,
                        @AuthenticationPrincipal UserDetails principal,
                        Model model) {
                AppUser user = getUser(principal);
                populateUserModel(user, model);

                ModuleSummary module = learningModuleRepository.findModuleById(moduleId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Module not found"));

                LearningModule lm = learningModuleRepository.findById(moduleId).orElse(null);
                int passMark = lm != null ? lm.getPassMarkPercent() : 60;

                AssessmentAttempt post = assessmentAttemptRepository
                                .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                                                user.getId(), moduleId, "POST", "SUBMITTED")
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "Post-test not yet submitted"));

                if (post.getScorePercent() == null || post.getScorePercent().intValue() < passMark) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "Passing post-test required to view certificate");
                }

                String certId = "SURAKSHA-" + module.getHazardCode() + "-" + String.format("%04d", user.getId());

                model.addAttribute("module", module);
                model.addAttribute("score", post.getScorePercent().intValue());
                model.addAttribute("issuedAt",
                                post.getSubmittedAt() != null ? post.getSubmittedAt() : LocalDateTime.now());
                model.addAttribute("certificateId", certId);

                return "certificate-view";
        }

        private AppUser getUser(UserDetails principal) {
                return appUserRepository.findByUsername(principal.getUsername())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "User not found"));
        }

        private void populateUserModel(AppUser user, Model model) {
                String[] nameParts = user.getFullName().trim().split("\\s+");
                String initials = nameParts.length >= 2
                                ? String.valueOf(nameParts[0].charAt(0))
                                                + String.valueOf(nameParts[nameParts.length - 1].charAt(0))
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
