package com.tailor_db.app.controller;

import com.tailor_db.app.service.InventoryService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final UserService userService;

    public InventoryController(InventoryService inventoryService, UserService userService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", inventoryService.getAllItems());
        return "inventory/list";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "inventory/create";
    }

    @PostMapping
    public String createItem(@RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        inventoryService.createItem(formData, tailorId);
        return "redirect:/inventory";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Map<String, Object> item = inventoryService.getItemById(id);
        model.addAttribute("item", item);
        return "inventory/edit";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable int id, @RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        inventoryService.updateItem(id, formData, tailorId);
        return "redirect:/inventory";
    }
}