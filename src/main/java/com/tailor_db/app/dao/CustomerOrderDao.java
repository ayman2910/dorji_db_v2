package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerOrderDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getStyleDetails(int styleId) {
        String sql = "SELECT * FROM STYLE_TEMPLATE WHERE Style_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, styleId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> getMeasurementRequirements(int styleId) {
        String sql = "SELECT Measurement_Name FROM STYLE_MEASUREMENT_REQUIREMENT WHERE Style_ID = ? ORDER BY Measurement_Name";
        return jdbcTemplate.queryForList(sql, styleId);
    }

    /**
     * Creates a web order with Tailor_ID = NULL and Order_Status = 'PENDING_APPROVAL'.
     * Returns the generated Order_ID.
     */
    public int createWebOrder(int customerId, int styleId, String outfitType,
                              BigDecimal totalPrice, BigDecimal estLaborHours) {
        String sql = "INSERT INTO OUTFIT_ORDER (Customer_ID, Tailor_ID, Style_ID, Outfit_Type, " +
                "Order_Date, Delivery_Date, Est_Labor_Hours, Order_Status, Total_Price, Advance_Paid) " +
                "VALUES (?, NULL, ?, ?, CURRENT_DATE, CURRENT_DATE, ?, 'PENDING_APPROVAL', ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ps.setInt(2, styleId);
            ps.setString(3, outfitType);
            ps.setBigDecimal(4, estLaborHours);
            ps.setBigDecimal(5, totalPrice);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated Order_ID");
        }
        return key.intValue();
    }

    public void insertMeasurement(int orderId, String bodyPart, String inchValue) {
        String sql = "INSERT INTO MEASUREMENT (Order_ID, Body_Part, Inch_Value) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, orderId, bodyPart, inchValue);
    }

    public void insertFabric(int orderId, String materialType, String color, double lengthMeters) {
        String sql = "INSERT INTO FABRIC_MATERIAL (Order_ID, Material_Type, Color, Length_Meters) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, orderId, materialType, color, lengthMeters);
    }
}
