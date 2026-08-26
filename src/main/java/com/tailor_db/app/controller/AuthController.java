package com.tailor_db.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Authentication authentication, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        boolean isTailor = authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TAILOR"));
        model.addAttribute("isTailor", isTailor);
        return "dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
