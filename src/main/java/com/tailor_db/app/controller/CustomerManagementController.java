package com.tailor_db.app.controller;

import com.tailor_db.app.service.CustomerManagementService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/customers")
public class CustomerManagementController {

    private final CustomerManagementService customerManagementService;
    private final UserService userService;

    public CustomerManagementController(CustomerManagementService customerManagementService, UserService userService) {
        this.customerManagementService = customerManagementService;
        this.userService = userService;
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerManagementService.getAllCustomers());
        return "customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "customers/create";
    }

    @PostMapping
    public String registerWalkInCustomer(@RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        customerManagementService.registerWalkInCustomer(formData, tailorId);
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable("id") int id, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        customerManagementService.deleteCustomer(id, tailorId);
        return "redirect:/customers";
    }
}
