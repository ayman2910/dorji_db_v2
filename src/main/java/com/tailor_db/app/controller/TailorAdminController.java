package com.tailor_db.app.controller;

import com.tailor_db.app.service.AdminWebOrderService;
import com.tailor_db.app.service.TailorAdminService;
import com.tailor_db.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class TailorAdminController {

    private final TailorAdminService tailorAdminService;
    private final AdminWebOrderService adminWebOrderService;
    private final UserService userService;

    public TailorAdminController(TailorAdminService tailorAdminService, AdminWebOrderService adminWebOrderService, UserService userService) {
        this.tailorAdminService = tailorAdminService;
        this.adminWebOrderService = adminWebOrderService;
        this.userService = userService;
    }

    private boolean checkAdmin(HttpSession session, Principal principal) {
        Object isMaster = session.getAttribute("isMasterAdmin");
        if (isMaster instanceof Boolean && (Boolean) isMaster) {
            return true;
        }
        if (principal != null) {
            Map<String, Object> user = userService.getUserByUsername(principal.getName());
            if (user != null && user.get("USER_ID") != null) {
                int userId = ((Number) user.get("USER_ID")).intValue();
                boolean master = tailorAdminService.isMasterAdmin(userId);
                session.setAttribute("isMasterAdmin", master);
                return master;
            }
        }
        return false;
    }

    @GetMapping("/approvals")
    public String listPendingActions(HttpSession session, Principal principal, Model model) {
        if (!checkAdmin(session, principal)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("pendingTailors", tailorAdminService.getPending());
        model.addAttribute("pendingOrders", adminWebOrderService.getPendingOrders());
        return "admin/approvals";
    }

    @PostMapping("/tailors/{id}/approve")
    public String approveTailor(@PathVariable("id") int id, HttpSession session, Principal principal) {
        if (!checkAdmin(session, principal)) {
            return "redirect:/dashboard";
        }
        tailorAdminService.approve(id);
        return "redirect:/admin/approvals";
    }

    @PostMapping("/tailors/{id}/reject")
    public String rejectTailor(@PathVariable("id") int id, HttpSession session, Principal principal) {
        if (!checkAdmin(session, principal)) {
            return "redirect:/dashboard";
        }
        tailorAdminService.deleteTailor(id);
        return "redirect:/admin/approvals";
    }
}
