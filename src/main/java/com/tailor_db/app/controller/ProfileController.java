package com.tailor_db.app.controller;

import com.tailor_db.app.service.ProfileService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(Model model, Authentication auth) {
        Map<String, Object> user = userService.getUserByUsername(auth.getName());
        int userId = ((Number) user.get("USER_ID")).intValue();

        model.addAttribute("profile", profileService.getProfile(userId));
        model.addAttribute("myLogs", profileService.getPersonalLogs(userId));
        return "profile";
    }

    @PostMapping
    public String updateProfile(@RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> user = userService.getUserByUsername(auth.getName());
        int userId = ((Number) user.get("USER_ID")).intValue();

        profileService.updateProfile(userId, formData);
        return "redirect:/profile";
    }
}
