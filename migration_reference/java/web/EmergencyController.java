package com.suraksha.suraksha.web;

import com.suraksha.suraksha.drill.EvacuationRoute;
import com.suraksha.suraksha.drill.EvacuationRouteRepository;
import com.suraksha.suraksha.emergency.*;
import com.suraksha.suraksha.user.AppUser;
import com.suraksha.suraksha.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class EmergencyController {

    public record ChecklistItemView(
            Long id,
            Long hazardId,
            String itemText,
            int sortOrder,
            boolean isChecked,
            LocalDate checkedOn) {
    }

    private final AppUserRepository appUserRepository;
    private final EmergencyPlanRepository emergencyPlanRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistStatusRepository checklistStatusRepository;
    private final EvacuationRouteRepository evacuationRouteRepository;

    public EmergencyController(AppUserRepository appUserRepository,
                               EmergencyPlanRepository emergencyPlanRepository,
                               EmergencyContactRepository emergencyContactRepository,
                               ChecklistItemRepository checklistItemRepository,
                               ChecklistStatusRepository checklistStatusRepository,
                               EvacuationRouteRepository evacuationRouteRepository) {
        this.appUserRepository = appUserRepository;
        this.emergencyPlanRepository = emergencyPlanRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.checklistStatusRepository = checklistStatusRepository;
        this.evacuationRouteRepository = evacuationRouteRepository;
    }

    @GetMapping("/emergency")
    public String showEmergencyPlan(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        populateUserModel(user, model);

        Long instId = user.getInstitutionId() != null ? user.getInstitutionId() : 1L;

        // Emergency Contacts
        List<EmergencyContact> contacts = emergencyContactRepository.findContactsForInstitution(instId);
        model.addAttribute("contacts", contacts);

        // Evacuation Routes
        List<EvacuationRoute> routes = evacuationRouteRepository.findByInstitutionIdOrderBySortOrder(instId);
        if (routes.isEmpty()) {
            routes = evacuationRouteRepository.findAllByOrderBySortOrder();
        }
        model.addAttribute("evacuationRoutes", routes);

        // Emergency Plan
        Optional<EmergencyPlan> planOpt = emergencyPlanRepository.findByInstitutionId(instId);
        String planContent = planOpt.map(EmergencyPlan::getContent).orElse(
                "## 1. Objectives & Scope\n" +
                "This Emergency Preparedness & Response Plan provides mandatory standard operating procedures (SOPs) for Delhi Public School Bhopal in accordance with National Disaster Management Authority (NDMA) guidelines and the Disaster Management Act, 2005.\n\n" +
                "## 2. Emergency Response Roles\n" +
                "- Incident Commander (Principal / Safety Officer): Oversees campus evacuation and coordinates with civil defense (112).\n" +
                "- Floor Wardens (Teachers & Faculty): Direct students quietly to secondary stairs and emergency exit routes; ensure no one is left behind.\n" +
                "- First Aid & Triage Team: Set up first-aid station at the primary assembly ground.\n\n" +
                "## 3. Hazard Specific Standard Operating Procedures\n" +
                "### Earthquake\n" +
                "1. Drop, Cover, and Hold On under heavy desks away from glass windows.\n" +
                "2. When tremors cease, evacuate in orderly single files via designated emergency stairs — never use elevators.\n" +
                "3. Assemble at the Main Sports Ground away from electric poles and overhead wires.\n\n" +
                "### Fire & Smoke\n" +
                "1. Sound the manual fire alarm and immediately dial Fire Services (101 / 112).\n" +
                "2. Stay low below rising toxic smoke; feel closed doors for heat before turning handles.\n" +
                "3. Evacuate to North Open Field and take headcounts.\n\n" +
                "### Flood / Flash Inundation\n" +
                "1. Move immediately to upper floors (Floor 2 and Terrace) if ground level is breached.\n" +
                "2. Cut main electrical circuit breakers to prevent electrocution.\n" +
                "3. Await NDRF rescue boats; never walk or drive through moving water."
        );
        model.addAttribute("emergencyPlanContent", planContent);
        model.addAttribute("planLastUpdated", planOpt.map(EmergencyPlan::getUpdatedAt).orElse(LocalDateTime.now()));

        // Safety Checklist
        List<ChecklistItem> items = checklistItemRepository.findAllByOrderBySortOrderAsc();
        List<ChecklistStatus> statuses = checklistStatusRepository.findByIdInstitutionId(instId);
        Map<Long, ChecklistStatus> statusMap = new HashMap<>();
        for (ChecklistStatus cs : statuses) {
            statusMap.put(cs.getId().getItemId(), cs);
        }

        List<ChecklistItemView> checklistViews = new ArrayList<>();
        int checkedCount = 0;
        for (ChecklistItem item : items) {
            ChecklistStatus cs = statusMap.get(item.getId());
            boolean isChecked = cs != null && cs.isChecked();
            if (isChecked) checkedCount++;
            checklistViews.add(new ChecklistItemView(
                    item.getId(),
                    item.getHazardId(),
                    item.getItemText(),
                    item.getSortOrder(),
                    isChecked,
                    cs != null ? cs.getCheckedOn() : null
            ));
        }

        int totalCount = items.size();
        int checklistPct = totalCount > 0 ? (int) Math.round((double) checkedCount * 100 / totalCount) : 0;

        model.addAttribute("checklistItems", checklistViews);
        model.addAttribute("checklistCheckedCount", checkedCount);
        model.addAttribute("checklistTotalCount", totalCount);
        model.addAttribute("checklistPct", checklistPct);

        return "emergency";
    }

    @PostMapping("/emergency/checklist/toggle")
    public String toggleChecklist(@RequestParam("itemId") Long itemId,
                                  @RequestParam(value = "checked", defaultValue = "false") boolean checked,
                                  @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        Long instId = user.getInstitutionId() != null ? user.getInstitutionId() : 1L;

        ChecklistStatusId id = new ChecklistStatusId(instId, itemId);
        ChecklistStatus cs = checklistStatusRepository.findById(id).orElse(new ChecklistStatus(id, false, null, null));
        cs.setChecked(checked);
        cs.setCheckedBy(user.getId());
        cs.setCheckedOn(LocalDate.now());
        checklistStatusRepository.save(cs);

        return "redirect:/emergency";
    }

    @PostMapping("/emergency/plan")
    public String updateEmergencyPlan(@RequestParam("content") String content,
                                      @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        Long instId = user.getInstitutionId() != null ? user.getInstitutionId() : 1L;

        EmergencyPlan plan = emergencyPlanRepository.findByInstitutionId(instId)
                .orElse(new EmergencyPlan(instId, content, user.getId()));
        plan.setContent(content);
        plan.setUpdatedBy(user.getId());
        plan.setUpdatedAt(LocalDateTime.now());
        emergencyPlanRepository.save(plan);

        return "redirect:/emergency";
    }

    private AppUser getUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User record not found"));
    }

    private void populateUserModel(AppUser user, Model model) {
        model.addAttribute("userName", user.getFullName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("roleLabel", user.getRole().name().replace('_', ' '));
        String initials = "";
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            String[] parts = user.getFullName().trim().split("\\s+");
            if (parts.length == 1) {
                initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                initials = ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
            }
        }
        model.addAttribute("userInitials", initials.isEmpty() ? "U" : initials);
    }
}

