package com.tailor_db.app.controller;

import com.tailor_db.app.service.StyleService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/styles")
public class StyleController {

    private final StyleService styleService;
    private final UserService userService;

    public StyleController(StyleService styleService, UserService userService) {
        this.styleService = styleService;
        this.userService = userService;
    }

    @GetMapping
    public String listStyles(Model model) {
        model.addAttribute("styles", styleService.getAllStyles());
        return "styles/list";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "styles/create";
    }

    @PostMapping
    public String createStyle(@RequestParam Map<String, String> formData) {
        styleService.createStyle(formData);
        return "redirect:/styles";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Map<String, Object> style = styleService.getStyleById(id);
        model.addAttribute("style", style);
        return "styles/edit";
    }

    @PostMapping("/{id}")
    public String updateStyle(@PathVariable int id, @RequestParam Map<String, String> formData) {
        styleService.updateStyle(id, formData);
        return "redirect:/styles";
    }

    @PostMapping("/{id}/delete")
    public String deleteStyle(@PathVariable int id, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        styleService.deleteStyle(id, tailorId);
        return "redirect:/styles";
    }

    @GetMapping("/{id}/measurements")
    public String showMeasurements(@PathVariable int id, Model model) {
        Map<String, Object> style = styleService.getStyleById(id);
        model.addAttribute("style", style);
        model.addAttribute("requirements", styleService.getRequirementsForStyle(id));
        return "styles/measurements";
    }

    @PostMapping("/{id}/measurements")
    public String addMeasurement(@PathVariable int id, @RequestParam String measurementName) {
        styleService.addMeasurementRequirement(id, measurementName);
        return "redirect:/styles/" + id + "/measurements";
    }

    @PostMapping("/{id}/measurements/delete")
    public String removeMeasurement(@PathVariable int id, @RequestParam String measurementName) {
        styleService.removeMeasurementRequirement(id, measurementName);
        return "redirect:/styles/" + id + "/measurements";
    }
}