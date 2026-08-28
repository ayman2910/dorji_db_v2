package com.tailor_db.app.controller;

import com.tailor_db.app.service.InventoryService;
import com.tailor_db.app.service.MaterialService;
import com.tailor_db.app.service.MeasurementService;
import com.tailor_db.app.service.OrderService;
import com.tailor_db.app.service.StyleService;
import com.tailor_db.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final StyleService styleService;
    private final MeasurementService measurementService;
    private final MaterialService materialService;
    private final InventoryService inventoryService;

    public OrderController(OrderService orderService, UserService userService, StyleService styleService,
                           MeasurementService measurementService, MaterialService materialService,
                           InventoryService inventoryService) {
        this.orderService = orderService;
        this.userService = userService;
        this.styleService = styleService;
        this.measurementService = measurementService;
        this.materialService = materialService;
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customers", userService.getCustomers());
        model.addAttribute("styles", styleService.getAllStyles());
        return "orders/create";
    }

    @PostMapping
    public String createOrder(@RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        orderService.createOutfitOrder(formData, tailorId);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable("id") int id, @RequestParam("status") String status, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        orderService.updateOrderStatus(id, status, tailorId);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/measurements")
    public String showMeasurements(@PathVariable int id, Model model) {
        model.addAttribute("measurements", measurementService.getMeasurementForm(id));
        model.addAttribute("orderId", id);
        return "orders/measurements";
    }

    @PostMapping("/{id}/measurements")
    public String saveMeasurements(@PathVariable int id, @RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        measurementService.saveOrderMeasurements(id, formData, tailorId);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/materials")
    public String showMaterials(@PathVariable int id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("orderId", id);
        model.addAttribute("fabrics", materialService.getFabrics(id));
        model.addAttribute("consumed", materialService.getConsumedItems(id));
        model.addAttribute("inventoryItems", inventoryService.getAllItems());
        return "orders/materials";
    }

    @PostMapping("/{id}/materials/fabric")
    public String addFabric(@PathVariable int id, @RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        materialService.addFabricToOrder(id, formData, tailorId);
        return "redirect:/orders/" + id + "/materials";
    }

    @PostMapping("/{id}/materials/consume")
    public String consumeInventory(@PathVariable int id, @RequestParam Map<String, String> formData, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        materialService.consumeInventoryForOrder(id, formData, tailorId);
        return "redirect:/orders/" + id + "/materials";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable int id, Authentication auth) {
        Map<String, Object> tailor = userService.getUserByUsername(auth.getName());
        int tailorId = ((Number) tailor.get("USER_ID")).intValue();
        orderService.deleteOrder(id, tailorId);
        return "redirect:/orders";
    }
}
