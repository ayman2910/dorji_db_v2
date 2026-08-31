package com.tailor_db.app.controller;

import com.tailor_db.app.service.CustomerPortalService;
import com.tailor_db.app.service.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final CustomerPortalService customerPortalService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    public CustomerPortalController(CustomerPortalService customerPortalService, UserService userService, JdbcTemplate jdbcTemplate) {
        this.customerPortalService = customerPortalService;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private int getCustomerId(Authentication auth) {
        Map<String, Object> user = userService.getUserByUsername(auth.getName());
        return ((Number) user.get("USER_ID")).intValue();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        int customerId = getCustomerId(auth);

        Map<String, Object> profile = customerPortalService.getCustomerProfile(customerId);
        
        java.util.List<Map<String, Object>> styles = customerPortalService.getStyleCatalog();
        for (Map<String, Object> style : styles) {
            int styleId = ((Number) style.get("Style_ID")).intValue();
            String imgSql = "SELECT Image_Path FROM STYLE_IMAGE WHERE Style_ID = ?";
            java.util.List<String> images = jdbcTemplate.queryForList(imgSql, String.class, styleId);
            
            if (images.isEmpty()) {
                images.add("/images/styles/default-style.png");
            }
            style.put("images", images);
        }

        model.addAttribute("profile", profile);
        model.addAttribute("orders", customerPortalService.getCustomerOrders(customerId));
        model.addAttribute("styles", styles);
        model.addAttribute("username", auth.getName());

        return "customer/dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam Map<String, String> formData,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        int customerId = getCustomerId(auth);
        customerPortalService.updateCustomerProfile(customerId, formData);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/customer/dashboard";
    }
}
