package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MeasurementDao {

    private final JdbcTemplate jdbcTemplate;

    public MeasurementDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getMeasurementFormForOrder(int orderId) {
        String sql = "SELECT smr.Measurement_Name AS Body_Part, m.Inch_Value " +
                "FROM OUTFIT_ORDER o " +
                "JOIN STYLE_MEASUREMENT_REQUIREMENT smr ON o.Style_ID = smr.Style_ID " +
                "LEFT JOIN MEASUREMENT m ON m.Order_ID = o.Order_ID AND m.Body_Part = smr.Measurement_Name " +
                "WHERE o.Order_ID = ? ORDER BY smr.Measurement_Name";
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public void deleteMeasurementsForOrder(int orderId) {
        String sql = "DELETE FROM MEASUREMENT WHERE Order_ID = ?";
        jdbcTemplate.update(sql, orderId);
    }

    public void insertMeasurement(int orderId, String bodyPart, String inchValue) {
        String sql = "INSERT INTO MEASUREMENT (Order_ID, Body_Part, Inch_Value) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, orderId, bodyPart, inchValue);
    }
}
