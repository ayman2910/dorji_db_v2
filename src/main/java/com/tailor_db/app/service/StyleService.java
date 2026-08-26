package com.tailor_db.app.service;

import com.tailor_db.app.dao.MeasurementRequirementDao;
import com.tailor_db.app.dao.StyleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class StyleService {

    private final StyleDao styleDao;
    private final MeasurementRequirementDao measurementRequirementDao;

    public StyleService(StyleDao styleDao, MeasurementRequirementDao measurementRequirementDao) {
        this.styleDao = styleDao;
        this.measurementRequirementDao = measurementRequirementDao;
    }

    public List<Map<String, Object>> getAllStyles() {
        return styleDao.findAll();
    }

    public Map<String, Object> getStyleById(int id) {
        return styleDao.findById(id);
    }

    @Transactional
    public void createStyle(Map<String, String> formData) {
        String name = formData.get("styleName");
        double price = parseDouble(formData.get("basePrice"));
        double hours = parseDouble(formData.get("estimatedHours"));
        styleDao.insert(name, price, hours);
    }

    @Transactional
    public void updateStyle(int id, Map<String, String> formData) {
        String name = formData.get("styleName");
        double price = parseDouble(formData.get("basePrice"));
        double hours = parseDouble(formData.get("estimatedHours"));
        styleDao.update(id, name, price, hours);
    }

    public List<Map<String, Object>> getRequirementsForStyle(int styleId) {
        return measurementRequirementDao.findByStyleId(styleId);
    }

    @Transactional
    public void addMeasurementRequirement(int styleId, String name) {
        measurementRequirementDao.addRequirement(styleId, name);
    }

    @Transactional
    public void removeMeasurementRequirement(int styleId, String name) {
        measurementRequirementDao.removeRequirement(styleId, name);
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