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

    public InventoryService(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    public List<Map<String, Object>> getAllItems() {
        return inventoryDao.findAll();
    }

    public Map<String, Object> getItemById(int id) {
        return inventoryDao.findById(id);
    }

    @Transactional
    public void createItem(Map<String, String> formData) {
        String name = formData.get("itemName");
        int qty = parseInt(formData.get("currentStockQty"));
        double cost = parseDouble(formData.get("unitCost"));
        int reorderLevel = parseInt(formData.get("reorderLevel"));
        inventoryDao.insert(name, qty, cost, reorderLevel);
    }

    @Transactional
    public void updateItem(int id, Map<String, String> formData) {
        String name = formData.get("itemName");
        int qty = parseInt(formData.get("currentStockQty"));
        double cost = parseDouble(formData.get("unitCost"));
        int reorderLevel = parseInt(formData.get("reorderLevel"));
        inventoryDao.update(id, name, qty, cost, reorderLevel);
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