package com.tailor_db.app.controller;

import com.tailor_db.app.service.DashboardService;
import com.tailor_db.app.service.TailorAdminService;
import com.tailor_db.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class AuthController {

    private final DashboardService dashboardService;
    private final TailorAdminService tailorAdminService;
    private final UserService userService;

    public AuthController(DashboardService dashboardService, TailorAdminService tailorAdminService, UserService userService) {
        this.dashboardService = dashboardService;
        this.tailorAdminService = tailorAdminService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Authentication authentication, Model model, HttpSession session) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            Map<String, Object> user = userService.getUserByUsername(principal.getName());
            if (user != null && user.get("USER_ID") != null) {
                int userId = ((Number) user.get("USER_ID")).intValue();
                session.setAttribute("isMasterAdmin", tailorAdminService.isMasterAdmin(userId));
            }
        }
        boolean isTailor = authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TAILOR"));
        model.addAttribute("isTailor", isTailor);
        if (isTailor) {
            model.addAttribute("stats", dashboardService.getDashboardStats());
        }
        return "dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}

