package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createOrder(Map<String, Object> orderData) {
        String sql = "INSERT INTO OUTFIT_ORDER (Customer_ID, Tailor_ID, Style_ID, Outfit_Type, Order_Date, Delivery_Date, Est_Labor_Hours, Order_Status, Total_Price, Advance_Paid) VALUES (?, ?, ?, ?, ?, ?, ?, 'MEASURED', ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, orderData.get("Customer_ID"));
            ps.setObject(2, orderData.get("Tailor_ID"));
            ps.setObject(3, orderData.get("Style_ID"));
            ps.setString(4, String.valueOf(orderData.get("Outfit_Type")));
            ps.setDate(5, toSqlDate(orderData.get("Order_Date")));
            ps.setDate(6, toSqlDate(orderData.get("Delivery_Date")));
            ps.setObject(7, orderData.get("Est_Labor_Hours"));
            ps.setObject(8, orderData.get("Total_Price"));
            ps.setObject(9, orderData.get("Advance_Paid"));
            return ps;
        };

        jdbcTemplate.update(psc, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated Order_ID");
        }
        return key.intValue();
    }

    public List<Map<String, Object>> findAllOrders() {
        String sql = "SELECT o.*, u.Username AS Customer_Name, s.Style_name FROM OUTFIT_ORDER o JOIN APP_USER u ON o.Customer_ID = u.USER_ID JOIN STYLE_TEMPLATE s ON o.Style_ID = s.Style_ID ORDER BY o.Order_Date DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> findById(int orderId) {
        String sql = "SELECT o.*, u.Username AS Customer_Name, s.Style_name FROM OUTFIT_ORDER o JOIN APP_USER u ON o.Customer_ID = u.USER_ID JOIN STYLE_TEMPLATE s ON o.Style_ID = s.Style_ID WHERE o.Order_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, orderId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE OUTFIT_ORDER SET Order_Status = ? WHERE Order_ID = ?";
        jdbcTemplate.update(sql, newStatus, orderId);
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM OUTFIT_ORDER WHERE Order_ID = ?";
        jdbcTemplate.update(sql, orderId);
    }

    private Date toSqlDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        if (value instanceof LocalDate localDate) {
            return Date.valueOf(localDate);
        }
        if (value instanceof String text) {
            return Date.valueOf(LocalDate.parse(text));
        }
        throw new IllegalArgumentException("Cannot convert value to SQL date: " + value);
    }
}
