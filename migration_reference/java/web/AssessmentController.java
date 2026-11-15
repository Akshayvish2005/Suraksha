package com.suraksha.suraksha.web;

import com.suraksha.suraksha.assessment.*;
import com.suraksha.suraksha.learning.LearningModule;
import com.suraksha.suraksha.learning.LearningModuleRepository;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AssessmentController {

    public record QuestionResultItem(
            QuestionView question,
            List<AnswerOptionView> options,
            Set<Long> selectedOptionIds,
            boolean isCorrect,
            String explanation) {
    }

    private final AppUserRepository appUserRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final AttemptAnswerOptionRepository attemptAnswerOptionRepository;
    private final QuestionRepository questionRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final LearningModuleRepository learningModuleRepository;

    public AssessmentController(AppUserRepository appUserRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            AttemptAnswerRepository attemptAnswerRepository,
            AttemptAnswerOptionRepository attemptAnswerOptionRepository,
            QuestionRepository questionRepository,
            ModuleProgressRepository moduleProgressRepository,
            LearningModuleRepository learningModuleRepository) {
        this.appUserRepository = appUserRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.attemptAnswerOptionRepository = attemptAnswerOptionRepository;
        this.questionRepository = questionRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.learningModuleRepository = learningModuleRepository;
    }

    public record AssessmentCardItem(
            Long moduleId,
            String moduleTitle,
            String hazardCode,
            String hazardName,
            int passMarkPercent,
            boolean preTestTaken,
            Integer preTestScore,
            Long preTestAttemptId,
            boolean lessonsComplete,
            long lessonsCompletedCount,
            long lessonsTotalCount,
            boolean postTestTaken,
            Integer postTestScore,
            boolean postTestPassed,
            Long postTestAttemptId) {
    }

    @GetMapping("/assessments")
    public String listAssessments(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        List<com.suraksha.suraksha.learning.ModuleSummary> modules = learningModuleRepository
                .findPublishedModulesEnglish();
        List<AssessmentCardItem> cards = new ArrayList<>();

        for (com.suraksha.suraksha.learning.ModuleSummary m : modules) {
            LearningModule lm = learningModuleRepository.findById(m.getId()).orElse(null);
            int passMark = lm != null ? lm.getPassMarkPercent() : 60;

            long totalLessons = moduleProgressRepository.countPublishedLessonsInModule(m.getId());
            long completedLessons = moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(),
                    m.getId());
            boolean lessonsComplete = (totalLessons > 0 && completedLessons >= totalLessons);

            Optional<AssessmentAttempt> preAttempt = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), m.getId(), "PRE", "SUBMITTED");
            boolean preTaken = preAttempt.isPresent();
            Integer preScore = preAttempt.map(a -> a.getScorePercent() != null ? a.getScorePercent().intValue() : 0)
                    .orElse(null);
            Long preId = preAttempt.map(AssessmentAttempt::getId).orElse(null);

            Optional<AssessmentAttempt> postAttempt = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), m.getId(), "POST", "SUBMITTED");
            boolean postTaken = postAttempt.isPresent();
            Integer postScore = postAttempt.map(a -> a.getScorePercent() != null ? a.getScorePercent().intValue() : 0)
                    .orElse(null);
            boolean postPassed = (postScore != null && postScore >= passMark);
            Long postId = postAttempt.map(AssessmentAttempt::getId).orElse(null);

            cards.add(new AssessmentCardItem(
                    m.getId(),
                    m.getTitle(),
                    m.getHazardCode(),
                    m.getHazardName(),
                    passMark,
                    preTaken,
                    preScore,
                    preId,
                    lessonsComplete,
                    completedLessons,
                    totalLessons,
                    postTaken,
                    postScore,
                    postPassed,
                    postId));
        }

        model.addAttribute("assessmentCards", cards);
        return "assessments";
    }

    @GetMapping("/modules/{moduleId}/pre-test")
    public String startPreTest(@PathVariable("moduleId") Long moduleId,
            @RequestParam(name = "retry", required = false) Boolean retry,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);

        if (!Boolean.TRUE.equals(retry)) {
            Optional<AssessmentAttempt> submitted = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), moduleId, "PRE", "SUBMITTED");
            if (submitted.isPresent()) {
                return "redirect:/assessment/" + submitted.get().getId() + "/result";
            }
        }

        Optional<AssessmentAttempt> inProgress = assessmentAttemptRepository
                .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                        user.getId(), moduleId, "PRE", "IN_PROGRESS");
        if (inProgress.isPresent() && !Boolean.TRUE.equals(retry)) {
            AssessmentAttempt attempt = inProgress.get();
            List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(moduleId, "PRE");
            for (int i = 0; i < questions.size(); i++) {
                if (!attemptAnswerRepository.existsByAttemptIdAndQuestionId(attempt.getId(),
                        questions.get(i).getId())) {
                    return "redirect:/assessment/" + attempt.getId() + "/question/" + (i + 1);
                }
            }
            return "redirect:/assessment/" + attempt.getId() + "/result";
        }

        List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(moduleId, "PRE");
        if (questions.isEmpty()) {
            return "redirect:/modules/" + moduleId;
        }

        AssessmentAttempt newAttempt = new AssessmentAttempt(
                user.getId(), moduleId, "PRE", "IN_PROGRESS", LocalDateTime.now());
        newAttempt.setTotalQuestions(questions.size());
        newAttempt = assessmentAttemptRepository.save(newAttempt);

        return "redirect:/assessment/" + newAttempt.getId() + "/question/1";
    }

    @GetMapping("/modules/{moduleId}/post-test")
    public String startPostTest(@PathVariable("moduleId") Long moduleId,
            @RequestParam(name = "retry", required = false) Boolean retry,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);

        long completed = moduleProgressRepository.countCompletedLessonsForUserModule(user.getId(), moduleId);
        long total = moduleProgressRepository.countPublishedLessonsInModule(moduleId);
        if (total == 0 || completed < total) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "All lessons must be completed before taking the post-test");
        }

        if (!Boolean.TRUE.equals(retry)) {
            Optional<AssessmentAttempt> submitted = assessmentAttemptRepository
                    .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                            user.getId(), moduleId, "POST", "SUBMITTED");
            if (submitted.isPresent()) {
                return "redirect:/assessment/" + submitted.get().getId() + "/result";
            }
        }

        Optional<AssessmentAttempt> inProgress = assessmentAttemptRepository
                .findTopByUserIdAndModuleIdAndAttemptTypeAndStatusOrderByStartedAtDesc(
                        user.getId(), moduleId, "POST", "IN_PROGRESS");
        if (inProgress.isPresent() && !Boolean.TRUE.equals(retry)) {
            AssessmentAttempt attempt = inProgress.get();
            List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(moduleId, "POST");
            for (int i = 0; i < questions.size(); i++) {
                if (!attemptAnswerRepository.existsByAttemptIdAndQuestionId(attempt.getId(),
                        questions.get(i).getId())) {
                    return "redirect:/assessment/" + attempt.getId() + "/question/" + (i + 1);
                }
            }
            return "redirect:/assessment/" + attempt.getId() + "/result";
        }

        List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(moduleId, "POST");
        if (questions.isEmpty()) {
            return "redirect:/modules/" + moduleId;
        }

        AssessmentAttempt newAttempt = new AssessmentAttempt(
                user.getId(), moduleId, "POST", "IN_PROGRESS", LocalDateTime.now());
        newAttempt.setTotalQuestions(questions.size());
        newAttempt = assessmentAttemptRepository.save(newAttempt);

        return "redirect:/assessment/" + newAttempt.getId() + "/question/1";
    }

    @GetMapping("/assessment/{attemptId}/question/{n}")
    public String showQuestion(@PathVariable("attemptId") Long attemptId,
            @PathVariable("n") int n,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        AssessmentAttempt attempt = assessmentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(
                attempt.getModuleId(), attempt.getAttemptType());

        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions found for this assessment");
        }

        if (n < 1 || n > questions.size()) {
            return "redirect:/assessment/" + attemptId + "/question/1";
        }

        QuestionView question = questions.get(n - 1);
        List<AnswerOptionView> options = questionRepository.findOptionsForQuestion(question.getId());

        boolean alreadyAnswered = attemptAnswerRepository.existsByAttemptIdAndQuestionId(attemptId, question.getId());
        Set<Long> selectedOptionIds = new HashSet<>();
        if (alreadyAnswered) {
            attemptAnswerRepository.findByAttemptIdAndQuestionId(attemptId, question.getId())
                    .ifPresent(answer -> {
                        List<AttemptAnswerOption> opts = attemptAnswerOptionRepository
                                .findByAttemptAnswerId(answer.getId());
                        for (AttemptAnswerOption opt : opts) {
                            selectedOptionIds.add(opt.getOptionId());
                        }
                    });
        }

        populateUserModel(user, model);
        model.addAttribute("attempt", attempt);
        model.addAttribute("question", question);
        model.addAttribute("options", options);
        model.addAttribute("questionNumber", n);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("alreadyAnswered", alreadyAnswered);
        model.addAttribute("selectedOptionIds", selectedOptionIds);

        return "assessment";
    }

    @PostMapping("/assessment/{attemptId}/question/{n}")
    public String submitAnswer(@PathVariable("attemptId") Long attemptId,
            @PathVariable("n") int n,
            @RequestParam(name = "optionId", required = false) List<Long> optionIds,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        AssessmentAttempt attempt = assessmentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        if ("SUBMITTED".equals(attempt.getStatus())) {
            return "redirect:/assessment/" + attemptId + "/result";
        }

        List<QuestionView> questions = questionRepository.findPublishedQuestionsForModule(
                attempt.getModuleId(), attempt.getAttemptType());

        if (n < 1 || n > questions.size()) {
            return "redirect:/assessment/" + attemptId + "/question/1";
        }

        QuestionView question = questions.get(n - 1);
        List<AnswerOptionView> options = questionRepository.findOptionsForQuestion(question.getId());

        if (optionIds == null || optionIds.isEmpty()) {
            return "redirect:/assessment/" + attemptId + "/question/" + n;
        }
        // Validate that every submitted option belongs to this question.
        Set<Long> validOptionIds = options.stream()
            .map(AnswerOptionView::getId)
            .collect(Collectors.toSet());
        Set<Long> chosenIds = new HashSet<>(optionIds);

        // Reject duplicate or invalid option IDs.
        if (chosenIds.size() != optionIds.size() || !validOptionIds.containsAll(chosenIds)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid answer options");
        }
        // MCQ and TRUE_FALSE must contain exactly one selected option.
        if (!"MULTI_SELECT".equalsIgnoreCase(question.getQType()) && optionIds.size() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select exactly one option");
        }
        boolean isCorrect;
        if ("MULTI_SELECT".equalsIgnoreCase(question.getQType())) {
            Set<Long> correctOptionIds = options.stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .map(AnswerOptionView::getId)
                    .collect(Collectors.toSet());

            isCorrect = correctOptionIds.equals(chosenIds);
        } else {
            Long chosenId = optionIds.get(0);
            isCorrect = options.stream()
                    .anyMatch(o -> o.getId().equals(chosenId) && Boolean.TRUE.equals(o.getIsCorrect()));
        }

        AttemptAnswer answer = attemptAnswerRepository.findByAttemptIdAndQuestionId(attemptId, question.getId())
                .orElseGet(() -> new AttemptAnswer(attemptId, question.getId(), isCorrect, LocalDateTime.now()));
        answer.setCorrect(isCorrect);
        answer.setAnsweredAt(LocalDateTime.now());
        answer = attemptAnswerRepository.save(answer);

        for (Long optId : optionIds) {
            AttemptAnswerOptionId aaoId = new AttemptAnswerOptionId(answer.getId(), optId);
            if (!attemptAnswerOptionRepository.existsById(aaoId)) {
                attemptAnswerOptionRepository.save(new AttemptAnswerOption(answer.getId(), optId));
            }
        }

        int totalQuestions = questions.size();
        if (n < totalQuestions) {
            return "redirect:/assessment/" + attemptId + "/question/" + (n + 1);
        } else {
            List<AttemptAnswer> allAnswers = attemptAnswerRepository.findByAttemptId(attemptId);
            long correctCount = allAnswers.stream().filter(AttemptAnswer::isCorrect).count();
            BigDecimal scorePercent = totalQuestions > 0
                    ? BigDecimal.valueOf(correctCount * 100.0 / totalQuestions).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            attempt.setStatus("SUBMITTED");
            attempt.setTotalQuestions(totalQuestions);
            attempt.setCorrectCount((int) correctCount);
            attempt.setScorePercent(scorePercent);
            attempt.setSubmittedAt(LocalDateTime.now());
            assessmentAttemptRepository.save(attempt);

            return "redirect:/assessment/" + attemptId + "/result";
        }
    }

    @GetMapping("/assessment/{attemptId}/result")
    public String showResult(@PathVariable("attemptId") Long attemptId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        AssessmentAttempt attempt = assessmentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this attempt");
        }

        LearningModule module = learningModuleRepository.findById(attempt.getModuleId())
                .orElse(null);
        int passMark = module != null ? module.getPassMarkPercent() : 60;

        BigDecimal scorePercent = attempt.getScorePercent() != null ? attempt.getScorePercent() : BigDecimal.ZERO;
        boolean passed = scorePercent.compareTo(BigDecimal.valueOf(passMark)) >= 0;

        List<AttemptAnswer> answers = attemptAnswerRepository.findByAttemptId(attemptId);
        List<QuestionResultItem> questionResults = new ArrayList<>();

        for (AttemptAnswer ans : answers) {
            QuestionView qView = questionRepository.findQuestionViewById(ans.getQuestionId()).orElse(null);
            List<AnswerOptionView> options = questionRepository.findOptionsForQuestion(ans.getQuestionId());
            Set<Long> selectedOptionIds = attemptAnswerOptionRepository.findByAttemptAnswerId(ans.getId())
                    .stream()
                    .map(AttemptAnswerOption::getOptionId)
                    .collect(Collectors.toSet());

            questionResults.add(new QuestionResultItem(
                    qView,
                    options,
                    selectedOptionIds,
                    ans.isCorrect(),
                    qView != null ? qView.getExplanation() : null));
        }

        populateUserModel(user, model);
        model.addAttribute("attempt", attempt);
        model.addAttribute("questionResults", questionResults);
        model.addAttribute("scorePercent", scorePercent);
        model.addAttribute("passed", passed);
        model.addAttribute("passMarkPercent", passMark);
        model.addAttribute("module", module);

        return "assessment-result";
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
