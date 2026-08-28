package com.tailor_db.app.service;

import com.tailor_db.app.dao.OrderDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderDao orderDao;
    private final StyleService styleService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public OrderService(OrderDao orderDao, StyleService styleService, UserService userService, AuditLogService auditLogService) {
        this.orderDao = orderDao;
        this.styleService = styleService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public int createOutfitOrder(Map<String, String> formData, int tailorId) {
        int customerId = Integer.parseInt(formData.get("customerId"));
        int styleId = Integer.parseInt(formData.get("styleId"));
        LocalDate deliveryDate = LocalDate.parse(formData.get("deliveryDate"));
        BigDecimal advancePaid = parseDecimal(formData.get("advancePaid"));

        Map<String, Object> style = styleService.getStyleById(styleId);
        if (style == null) {
            throw new IllegalArgumentException("Style not found: " + styleId);
        }

        Object outfitType = style.get("Outfit_Type");
        if (outfitType == null) {
            outfitType = style.get("Style_name");
        }

        Object laborHours = style.get("Est_Labor_Hours");
        if (laborHours == null) {
            laborHours = style.get("Estimated_Labor_Hours");
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("Customer_ID", customerId);
        orderData.put("Tailor_ID", tailorId);
        orderData.put("Style_ID", styleId);
        orderData.put("Outfit_Type", outfitType);
        orderData.put("Order_Date", LocalDate.now());
        orderData.put("Delivery_Date", deliveryDate);
        orderData.put("Est_Labor_Hours", laborHours);
        orderData.put("Total_Price", style.get("Base_Price"));
        orderData.put("Advance_Paid", advancePaid);

        int orderId = orderDao.createOrder(orderData);

        auditLogService.logActivity(tailorId, "INSERT", "OUTFIT_ORDER", String.valueOf(orderId),
                null, "Created Order for Customer: " + formData.get("customerId"));

        return orderId;
    }

    public List<Map<String, Object>> getAllOrders() {
        return orderDao.findAllOrders();
    }

    public Map<String, Object> getOrderById(int orderId) {
        return orderDao.findById(orderId);
    }

    @Transactional
    public void updateOrderStatus(int orderId, String newStatus, int tailorId) {
        Map<String, Object> order = orderDao.findById(orderId);
        String oldStatus = order != null ? String.valueOf(order.get("Order_Status")) : "UNKNOWN";
        orderDao.updateOrderStatus(orderId, newStatus);
        auditLogService.logActivity(tailorId, "UPDATE", "OUTFIT_ORDER", String.valueOf(orderId), oldStatus, newStatus);
    }

    @Transactional
    public void deleteOrder(int orderId, int tailorId) {
        Map<String, Object> order = orderDao.findById(orderId);
        String orderSummary = order != null ? "Customer: " + order.get("Customer_Name") + ", Style: " + order.get("Style_name") : "ID: " + orderId;
        orderDao.deleteOrder(orderId);
        auditLogService.logActivity(tailorId, "DELETE", "OUTFIT_ORDER", String.valueOf(orderId), orderSummary, null);
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }
}
