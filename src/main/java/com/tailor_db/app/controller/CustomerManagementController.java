package com.tailor_db.app.controller;

import com.tailor_db.app.service.CustomerManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/customers")
public class CustomerManagementController {

    private final CustomerManagementService customerManagementService;

    public CustomerManagementController(CustomerManagementService customerManagementService) {
        this.customerManagementService = customerManagementService;
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
    public String registerWalkInCustomer(@RequestParam Map<String, String> formData) {
        customerManagementService.registerWalkInCustomer(formData);
        return "redirect:/customers";
    }
}
