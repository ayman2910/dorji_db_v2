package com.tailor_db.app.service;

import com.tailor_db.app.dao.MeasurementDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MeasurementService {

    private final MeasurementDao measurementDao;
    private final AuditLogService auditLogService;

    public MeasurementService(MeasurementDao measurementDao, AuditLogService auditLogService) {
        this.measurementDao = measurementDao;
        this.auditLogService = auditLogService;
    }

    public List<Map<String, Object>> getMeasurementForm(int orderId) {
        return measurementDao.getMeasurementFormForOrder(orderId);
    }

    @Transactional
    public void saveOrderMeasurements(int orderId, Map<String, String> formData, int tailorId) {
        measurementDao.deleteMeasurementsForOrder(orderId);

        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith("_csrf")) {
                continue;
            }
            String value = entry.getValue();
            if (value != null && !value.trim().isEmpty()) {
                measurementDao.insertMeasurement(orderId, key, value.trim());
            }
        }

        auditLogService.logActivity(tailorId, "INSERT", "MEASUREMENT", String.valueOf(orderId),
                null, "Saved/Updated measurements for order.");
    }
}
