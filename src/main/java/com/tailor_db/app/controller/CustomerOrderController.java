package com.tailor_db.app.controller;

import com.tailor_db.app.service.CustomerOrderService;
import com.tailor_db.app.service.CustomerPortalService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/order")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final CustomerPortalService customerPortalService;
    private final UserService userService;

    public CustomerOrderController(CustomerOrderService customerOrderService, CustomerPortalService customerPortalService, UserService userService) {
        this.customerOrderService = customerOrderService;
        this.customerPortalService = customerPortalService;
        this.userService = userService;
    }

    private int getCustomerId(Authentication auth) {
        Map<String, Object> user = userService.getUserByUsername(auth.getName());
        return ((Number) user.get("USER_ID")).intValue();
    }

    @GetMapping("/{id}")
    public String showOrderForm(@PathVariable("id") int styleId, Authentication auth, Model model) {
        Map<String, Object> style = customerOrderService.getStyleDetails(styleId);
        if (style == null) {
            return "redirect:/customer/dashboard";
        }

        int customerId = getCustomerId(auth);
        Map<String, Object> profile = customerPortalService.getCustomerProfile(customerId);
        String phone = profile != null ? (String) profile.get("Phone_Number") : "Not provided";

        List<Map<String, Object>> requirements = customerOrderService.getMeasurementRequirements(styleId);

        model.addAttribute("style", style);
        model.addAttribute("requirements", requirements);
        model.addAttribute("customerPhone", phone);

        return "customer/order-form";
    }

    @PostMapping("/{id}/submit")
    public String submitOrder(@PathVariable("id") int styleId,
                              @RequestParam Map<String, String> formData,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        int customerId = getCustomerId(auth);

        try {
            customerOrderService.submitWebOrder(customerId, styleId, formData);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your order has been submitted successfully! We will review it shortly.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to submit order: " + e.getMessage());
        }

        return "redirect:/customer/dashboard";
    }
}
