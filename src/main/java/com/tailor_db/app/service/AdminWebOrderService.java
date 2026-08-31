package com.tailor_db.app.service;

import com.tailor_db.app.dao.AdminWebOrderDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminWebOrderService {

    private final AdminWebOrderDao adminWebOrderDao;
    private final AuditLogService auditLogService;

    public AdminWebOrderService(AdminWebOrderDao adminWebOrderDao, AuditLogService auditLogService) {
        this.adminWebOrderDao = adminWebOrderDao;
        this.auditLogService = auditLogService;
    }

    public List<Map<String, Object>> getPendingOrders() {
        return adminWebOrderDao.getPendingWebOrders();
    }

    public Map<String, Object> getOrderById(int orderId) {
        return adminWebOrderDao.getOrderById(orderId);
    }

    public List<Map<String, Object>> getOrderMeasurements(int orderId) {
        return adminWebOrderDao.getOrderMeasurements(orderId);
    }

    public List<Map<String, Object>> getOrderFabrics(int orderId) {
        return adminWebOrderDao.getOrderFabrics(orderId);
    }

    public List<Map<String, Object>> getActiveTailors() {
        return adminWebOrderDao.getActiveTailors();
    }

    @Transactional
    public void approveWebOrder(int orderId, Map<String, String> formData, int adminId) {
        LocalDate deliveryDate = LocalDate.parse(formData.get("deliveryDate"));
        BigDecimal totalPrice = new BigDecimal(formData.get("totalPrice").trim());
        BigDecimal advancePaid = parseDecimal(formData.get("advancePaid"));
        int tailorId = Integer.parseInt(formData.get("tailorId"));

        adminWebOrderDao.approveOrder(orderId, deliveryDate, totalPrice, advancePaid, tailorId);

        auditLogService.logActivity(adminId, "UPDATE", "OUTFIT_ORDER", String.valueOf(orderId),
                "PENDING_APPROVAL", "MEASURED — Approved web order, assigned Tailor ID: " + tailorId);
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }
}
