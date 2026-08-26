package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MeasurementRequirementDao {

    private final JdbcTemplate jdbcTemplate;

    public MeasurementRequirementDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findByStyleId(int styleId) {
        String sql = "SELECT Measurement_Name FROM STYLE_MEASUREMENT_REQUIREMENT WHERE Style_ID = ? ORDER BY Measurement_Name";
        return jdbcTemplate.queryForList(sql, styleId);
    }

    public void addRequirement(int styleId, String measurementName) {
        String sql = "INSERT INTO STYLE_MEASUREMENT_REQUIREMENT (Style_ID, Measurement_Name) VALUES (?, ?)";
        jdbcTemplate.update(sql, styleId, measurementName);
    }

    public void removeRequirement(int styleId, String measurementName) {
        String sql = "DELETE FROM STYLE_MEASUREMENT_REQUIREMENT WHERE Style_ID = ? AND Measurement_Name = ?";
        jdbcTemplate.update(sql, styleId, measurementName);
    }
}