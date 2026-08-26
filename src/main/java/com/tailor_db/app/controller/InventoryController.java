package com.tailor_db.app.controller;

import com.tailor_db.app.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
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
    public String createItem(@RequestParam Map<String, String> formData) {
        inventoryService.createItem(formData);
        return "redirect:/inventory";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Map<String, Object> item = inventoryService.getItemById(id);
        model.addAttribute("item", item);
        return "inventory/edit";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable int id, @RequestParam Map<String, String> formData) {
        inventoryService.updateItem(id, formData);
        return "redirect:/inventory";
    }
}