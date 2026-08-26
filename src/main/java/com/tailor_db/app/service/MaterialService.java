package com.tailor_db.app.service;

import com.tailor_db.app.dao.MaterialDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MaterialService {

    private final MaterialDao materialDao;
    private final InventoryService inventoryService;

    public MaterialService(MaterialDao materialDao, InventoryService inventoryService) {
        this.materialDao = materialDao;
        this.inventoryService = inventoryService;
    }

    public List<Map<String, Object>> getFabrics(int orderId) {
        return materialDao.getFabricsForOrder(orderId);
    }

    public List<Map<String, Object>> getConsumedItems(int orderId) {
        return materialDao.getConsumedInventoryForOrder(orderId);
    }

    public List<Map<String, Object>> getAvailableItems() {
        return inventoryService.getAllItems();
    }

    public void addFabricToOrder(int orderId, Map<String, String> formData) {
        String type = formData.get("materialType");
        String color = formData.get("color");
        double length = parseDouble(formData.get("lengthMeters"));
        materialDao.addFabric(orderId, type, color, length);
    }

    @Transactional
    public void consumeInventoryForOrder(int orderId, Map<String, String> formData) {
        int itemId = Integer.parseInt(formData.get("itemId"));
        int quantityUsed = Integer.parseInt(formData.get("quantityUsed"));
        materialDao.recordInventoryConsumption(orderId, itemId, quantityUsed);
        materialDao.decrementInventoryStock(itemId, quantityUsed);
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        return new BigDecimal(value.trim()).doubleValue();
    }
}
