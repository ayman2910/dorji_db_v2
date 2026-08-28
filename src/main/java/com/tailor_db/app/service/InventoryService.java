package com.tailor_db.app.service;

import com.tailor_db.app.dao.InventoryDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final AuditLogService auditLogService;

    public InventoryService(InventoryDao inventoryDao, AuditLogService auditLogService) {
        this.inventoryDao = inventoryDao;
        this.auditLogService = auditLogService;
    }

    public List<Map<String, Object>> getAllItems() {
        return inventoryDao.findAll();
    }

    public Map<String, Object> getItemById(int id) {
        return inventoryDao.findById(id);
    }

    @Transactional
    public void createItem(Map<String, String> formData, int tailorId) {
        String name = formData.get("itemName");
        int qty = parseInt(formData.get("currentStockQty"));
        double cost = parseDouble(formData.get("unitCost"));
        int reorderLevel = parseInt(formData.get("reorderLevel"));
        inventoryDao.insert(name, qty, cost, reorderLevel);

        auditLogService.logActivity(tailorId, "INSERT", "INVENTORY_ITEM", name,
                null, "Added Qty: " + qty);
    }

    @Transactional
    public void updateItem(int id, Map<String, String> formData, int tailorId) {
        // Fetch old values before update
        Map<String, Object> oldItem = inventoryDao.findById(id);
        String oldItemName = oldItem != null ? String.valueOf(oldItem.get("Item_Name")) : "UNKNOWN";
        String oldQty = oldItem != null ? String.valueOf(oldItem.get("Current_Stock_Qty")) : "0";

        String name = formData.get("itemName");
        int qty = parseInt(formData.get("currentStockQty"));
        double cost = parseDouble(formData.get("unitCost"));
        int reorderLevel = parseInt(formData.get("reorderLevel"));
        inventoryDao.update(id, name, qty, cost, reorderLevel);

        // Log the change
        auditLogService.logActivity(tailorId, "UPDATE", "INVENTORY_ITEM", String.valueOf(id),
                oldItemName + " (Qty: " + oldQty + ")", name + " (Qty: " + qty + ")");
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return new BigDecimal(value.trim()).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}