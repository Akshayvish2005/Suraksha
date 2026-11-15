package com.suraksha.suraksha.web;

import com.suraksha.suraksha.drill.*;
import com.suraksha.suraksha.learning.Hazard;
import com.suraksha.suraksha.learning.HazardRepository;
import com.suraksha.suraksha.scenario.Scenario;
import com.suraksha.suraksha.scenario.ScenarioRepository;
import com.suraksha.suraksha.scenario.ScenarioSummary;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class DrillController {

    public record DrillCard(
            DrillSummary summary,
            String userStatus,
            String userRole,
            boolean canJoin,
            boolean isCompleted) {
    }

    private final AppUserRepository appUserRepository;
    private final DrillRepository drillRepository;
    private final DrillParticipantRepository drillParticipantRepository;
    private final EvacuationRouteRepository evacuationRouteRepository;
    private final ScenarioRepository scenarioRepository;
    private final HazardRepository hazardRepository;

    public DrillController(AppUserRepository appUserRepository,
            DrillRepository drillRepository,
            DrillParticipantRepository drillParticipantRepository,
            EvacuationRouteRepository evacuationRouteRepository,
            ScenarioRepository scenarioRepository,
            HazardRepository hazardRepository) {
        this.appUserRepository = appUserRepository;
        this.drillRepository = drillRepository;
        this.drillParticipantRepository = drillParticipantRepository;
        this.evacuationRouteRepository = evacuationRouteRepository;
        this.scenarioRepository = scenarioRepository;
        this.hazardRepository = hazardRepository;
    }

    @GetMapping("/drills")
    public String listDrills(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        Long institutionId = user.getInstitutionId() != null ? user.getInstitutionId() : 1L;
        List<DrillSummary> allDrills = drillRepository.findAllDrillSummaries(institutionId);

        LocalDateTime now = LocalDateTime.now();
        List<DrillCard> activeDrills = new ArrayList<>();
        List<DrillCard> upcomingDrills = new ArrayList<>();
        List<DrillCard> pastDrills = new ArrayList<>();

        for (DrillSummary ds : allDrills) {
            Optional<DrillParticipant> partOpt = drillParticipantRepository.findByDrillIdAndUserId(ds.getId(),
                    user.getId());
            String status = partOpt.map(DrillParticipant::getStatus)
                    .orElse(ds.getCampusWide() ? "ASSIGNED" : "NOT_ASSIGNED");
            String role = partOpt.map(DrillParticipant::getDrillRole).orElse("STUDENT");

            boolean inWindow = (ds.getStartTime() == null || !now.isBefore(ds.getStartTime()))
                    && (ds.getEndTime() == null || !now.isAfter(ds.getEndTime()));
            boolean isClosed = "CLOSED".equalsIgnoreCase(ds.getStatus())
                    || (ds.getEndTime() != null && now.isAfter(ds.getEndTime()));
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(status);
            boolean canJoin = inWindow && !isClosed && !isCompleted;

            DrillCard card = new DrillCard(ds, status, role, canJoin, isCompleted);

            if (isClosed) {
                pastDrills.add(card);
            } else if (inWindow || "OPEN".equalsIgnoreCase(ds.getStatus())) {
                activeDrills.add(card);
            } else {
                upcomingDrills.add(card);
            }
        }

        model.addAttribute("activeDrills", activeDrills);
        model.addAttribute("upcomingDrills", upcomingDrills);
        model.addAttribute("pastDrills", pastDrills);

        return "drills";
    }

    @GetMapping("/drills/{drillId}")
    public String drillDetail(@PathVariable("drillId") Long drillId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        DrillSummary drill = drillRepository.findDrillSummaryById(drillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drill not found"));

        Optional<DrillParticipant> partOpt = drillParticipantRepository.findByDrillIdAndUserId(drillId, user.getId());
        String participantStatus = partOpt.map(DrillParticipant::getStatus)
                .orElse(drill.getCampusWide() ? "ASSIGNED" : "NOT_ASSIGNED");
        String participantRole = partOpt.map(DrillParticipant::getDrillRole).orElse("STUDENT");

        LocalDateTime now = LocalDateTime.now();
        boolean inWindow = (drill.getStartTime() == null || !now.isBefore(drill.getStartTime()))
                && (drill.getEndTime() == null || !now.isAfter(drill.getEndTime()));
        boolean isClosed = "CLOSED".equalsIgnoreCase(drill.getStatus())
                || (drill.getEndTime() != null && now.isAfter(drill.getEndTime()));

        model.addAttribute("drill", drill);
        model.addAttribute("participantStatus", participantStatus);
        model.addAttribute("participantRole", participantRole);
        model.addAttribute("inWindow", inWindow);
        model.addAttribute("isClosed", isClosed);

        return "drill-detail";
    }

    @GetMapping("/drills/{drillId}/join")
    public String joinDrill(@PathVariable("drillId") Long drillId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        Drill drill = drillRepository.findById(drillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drill not found"));

        DrillParticipant participant = drillParticipantRepository.findByDrillIdAndUserId(drillId, user.getId())
                .orElseGet(() -> {
                    DrillParticipant newPart = new DrillParticipant(drillId, user.getId(), "STUDENT", "JOINED");
                    newPart.setJoinedAt(LocalDateTime.now());
                    return drillParticipantRepository.save(newPart);
                });

        if (participant.getJoinedAt() == null) {
            participant.setJoinedAt(LocalDateTime.now());
            participant.setStatus("JOINED");
            drillParticipantRepository.save(participant);
        }

        Long instId = drill.getInstitutionId() != null ? drill.getInstitutionId() : 1L;
        List<EvacuationRoute> routes = evacuationRouteRepository.findByInstitutionIdOrderBySortOrder(instId);
        if (routes.isEmpty()) {
            routes = evacuationRouteRepository.findAllByOrderBySortOrder();
        }

        Scenario scenario = scenarioRepository.findById(drill.getScenarioId()).orElse(null);

        model.addAttribute("drill", drill);
        model.addAttribute("participant", participant);
        model.addAttribute("routes", routes);
        model.addAttribute("scenario", scenario);

        return "drill-active";
    }

    @PostMapping("/drills/{drillId}/complete")
    public String completeDrill(@PathVariable("drillId") Long drillId,
            @RequestParam(name = "evacuationRouteId", required = false) Long evacuationRouteId,
            @RequestParam(name = "evacuationTimeSeconds", required = false, defaultValue = "120") Integer evacuationTimeSeconds,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);

        DrillParticipant participant = drillParticipantRepository.findByDrillIdAndUserId(drillId, user.getId())
                .orElseGet(() -> new DrillParticipant(drillId, user.getId(), "STUDENT", "JOINED"));

        participant.setEvacuationRouteId(evacuationRouteId);
        participant.setEvacuationTimeSeconds(evacuationTimeSeconds);
        participant.setCompletedAt(LocalDateTime.now());
        participant.setStatus("COMPLETED");
        drillParticipantRepository.save(participant);

        return "redirect:/drills/" + drillId + "/report";
    }

    @GetMapping("/drills/{drillId}/report")
    public String drillReport(@PathVariable("drillId") Long drillId,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        DrillSummary drill = drillRepository.findDrillSummaryById(drillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drill not found"));

        List<DrillParticipantDetail> participants = drillParticipantRepository.findParticipantDetailsByDrillId(drillId);

        long totalCount = participants.size();
        long completedCount = participants.stream().filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus())).count();
        long joinedCount = participants.stream()
                .filter(p -> "JOINED".equalsIgnoreCase(p.getStatus()) || "COMPLETED".equalsIgnoreCase(p.getStatus()))
                .count();

        double avgEvacTime = participants.stream()
                .filter(p -> p.getEvacuationTimeSeconds() != null && p.getEvacuationTimeSeconds() > 0)
                .mapToInt(DrillParticipantDetail::getEvacuationTimeSeconds)
                .average()
                .orElse(0.0);

        int participationPct = totalCount > 0 ? (int) Math.round((completedCount * 100.0) / totalCount) : 0;

        Optional<DrillParticipant> userPart = drillParticipantRepository.findByDrillIdAndUserId(drillId, user.getId());

        model.addAttribute("drill", drill);
        model.addAttribute("participants", participants);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("joinedCount", joinedCount);
        model.addAttribute("avgEvacTime", Math.round(avgEvacTime));
        model.addAttribute("participationPct", participationPct);
        model.addAttribute("userParticipant", userPart.orElse(null));

        return "drill-report";
    }

    @GetMapping("/drills/new")
    public String newDrillForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        List<Hazard> hazards = hazardRepository.findAll();
        List<ScenarioSummary> scenarios = scenarioRepository.findAllPublished();

        model.addAttribute("hazards", hazards);
        model.addAttribute("scenarios", scenarios);

        return "drill-create";
    }

    @PostMapping("/drills/new")
    public String createDrill(@RequestParam("title") String title,
            @RequestParam("hazardId") Long hazardId,
            @RequestParam("scenarioId") Long scenarioId,
            @RequestParam("startTime") String startTimeStr,
            @RequestParam("endTime") String endTimeStr,
            @RequestParam(value = "campusWide", defaultValue = "true") boolean campusWide,
            @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);

        LocalDateTime start = LocalDateTime.parse(startTimeStr);
        LocalDateTime end = LocalDateTime.parse(endTimeStr);

        Drill drill = new Drill(
                user.getInstitutionId() != null ? user.getInstitutionId() : 1L,
                hazardId,
                scenarioId,
                title,
                user.getId(),
                start,
                end,
                "SELF_PACED",
                campusWide,
                "SCHEDULED");
        drill = drillRepository.save(drill);

        // Pre-assign user as teacher/marshal
        DrillParticipant creatorPart = new DrillParticipant(drill.getId(), user.getId(), "FLOOR_WARDEN", "ASSIGNED");
        drillParticipantRepository.save(creatorPart);

        return "redirect:/drills";
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
