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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/admin/web-orders")
public class AdminWebOrderController {

    private final AdminWebOrderService adminWebOrderService;
    private final TailorAdminService tailorAdminService;
    private final UserService userService;

    public AdminWebOrderController(AdminWebOrderService adminWebOrderService,
                                   TailorAdminService tailorAdminService,
                                   UserService userService) {
        this.adminWebOrderService = adminWebOrderService;
        this.tailorAdminService = tailorAdminService;
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

    private int getAdminId(Principal principal) {
        Map<String, Object> user = userService.getUserByUsername(principal.getName());
        return ((Number) user.get("USER_ID")).intValue();
    }

    // The pending orders are now listed in TailorAdminController (unified approvals hub)

    @GetMapping("/{id}/review")
    public String reviewOrder(@PathVariable("id") int orderId,
                              HttpSession session, Principal principal, Model model) {
        if (!checkAdmin(session, principal)) {
            return "redirect:/dashboard";
        }

        Map<String, Object> order = adminWebOrderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/admin/approvals";
        }

        model.addAttribute("order", order);
        model.addAttribute("measurements", adminWebOrderService.getOrderMeasurements(orderId));
        model.addAttribute("fabrics", adminWebOrderService.getOrderFabrics(orderId));
        model.addAttribute("tailors", adminWebOrderService.getActiveTailors());

        return "admin/web-order-review";
    }

    @PostMapping("/{id}/approve")
    public String approveOrder(@PathVariable("id") int orderId,
                               @RequestParam Map<String, String> formData,
                               HttpSession session, Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, principal)) {
            return "redirect:/dashboard";
        }

        int adminId = getAdminId(principal);
        adminWebOrderService.approveWebOrder(orderId, formData, adminId);
        redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " approved successfully!");

        return "redirect:/admin/approvals";
    }
}
