package com.tailor_db.app.service;

import com.tailor_db.app.dao.CustomerOrderDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CustomerOrderService {

    private final CustomerOrderDao customerOrderDao;

    public CustomerOrderService(CustomerOrderDao customerOrderDao) {
        this.customerOrderDao = customerOrderDao;
    }

    public Map<String, Object> getStyleDetails(int styleId) {
        return customerOrderDao.getStyleDetails(styleId);
    }

    public List<Map<String, Object>> getMeasurementRequirements(int styleId) {
        return customerOrderDao.getMeasurementRequirements(styleId);
    }

    @Transactional
    public int submitWebOrder(int customerId, int styleId, Map<String, String> formData) {
        // Fetch style details for defaults
        Map<String, Object> style = customerOrderDao.getStyleDetails(styleId);
        if (style == null) {
            throw new IllegalArgumentException("Style not found: " + styleId);
        }

        String outfitType = String.valueOf(style.get("Style_name"));
        BigDecimal basePrice = (BigDecimal) style.get("Base_Price");
        BigDecimal laborHours = (BigDecimal) style.get("Estimated_Labor_Hours");

        // 1. Create the order
        int orderId = customerOrderDao.createWebOrder(customerId, styleId, outfitType, basePrice, laborHours);

        // 2. Insert measurements (form keys prefixed with "meas_")
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getKey().startsWith("meas_") && entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                String bodyPart = entry.getKey().substring(5); // Remove "meas_" prefix
                customerOrderDao.insertMeasurement(orderId, bodyPart, entry.getValue().trim());
            }
        }

        // 3. Insert fabric material
        String materialType = formData.get("materialType");
        String color = formData.get("color");
        String lengthStr = formData.get("lengthMeters");

        if (materialType != null && !materialType.trim().isEmpty()
                && color != null && !color.trim().isEmpty()
                && lengthStr != null && !lengthStr.trim().isEmpty()) {
            double length = Double.parseDouble(lengthStr.trim());
            customerOrderDao.insertFabric(orderId, materialType.trim(), color.trim(), length);
        }

        return orderId;
    }
}
