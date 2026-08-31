package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CustomerPortalDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerPortalDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getCustomerProfile(int userId) {
        String sql = "SELECT u.USER_ID, u.Username, u.First_name, u.Last_name, u.Role, " +
                "c.House_no, c.Street, c.City, " +
                "(SELECT p.Phone_Number FROM USER_PHONE p WHERE p.USER_ID = u.USER_ID LIMIT 1) AS Phone_Number " +
                "FROM APP_USER u JOIN CUSTOMER c ON u.USER_ID = c.USER_ID " +
                "WHERE u.USER_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> getCustomerOrders(int customerId) {
        String sql = "SELECT o.Order_ID, o.Style_ID, o.Order_Date, o.Delivery_Date, " +
                "o.Order_Status, o.Total_Price, o.Advance_Paid, o.Est_Labor_Hours, " +
                "s.Style_name " +
                "FROM OUTFIT_ORDER o " +
                "JOIN STYLE_TEMPLATE s ON o.Style_ID = s.Style_ID " +
                "WHERE o.Customer_ID = ? ORDER BY o.Order_Date DESC";
        return jdbcTemplate.queryForList(sql, customerId);
    }

    public List<Map<String, Object>> getStyleCatalog() {
        String sql = "SELECT s.Style_ID, s.Style_name, s.Base_Price, s.Estimated_Labor_Hours " +
                "FROM STYLE_TEMPLATE s ORDER BY s.Style_name ASC";
        List<Map<String, Object>> styles = jdbcTemplate.queryForList(sql);
        
        for (Map<String, Object> style : styles) {
            int styleId = ((Number) style.get("Style_ID")).intValue();
            String imgSql = "SELECT Image_Path FROM STYLE_IMAGE WHERE Style_ID = ?";
            List<String> images = jdbcTemplate.queryForList(imgSql, String.class, styleId);
            
            if (images.isEmpty()) {
                images.add("/images/styles/default-style.png");
            }
            style.put("images", images);
        }
        
        return styles;
    }

    public void updateUserNames(int userId, String firstName, String lastName) {
        String sql = "UPDATE APP_USER SET First_name = ?, Last_name = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql, firstName, lastName, userId);
    }

    public void updateCustomerAddress(int userId, String houseNo, String street, String city) {
        String sql = "UPDATE CUSTOMER SET House_no = ?, Street = ?, City = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql, houseNo, street, city, userId);
    }

    public void updatePhone(int userId, String phone) {
        jdbcTemplate.update("DELETE FROM USER_PHONE WHERE USER_ID = ?", userId);
        if (phone != null && !phone.trim().isEmpty()) {
            jdbcTemplate.update("INSERT INTO USER_PHONE (USER_ID, Phone_Number) VALUES (?, ?)", userId, phone.trim());
        }
    }
}
