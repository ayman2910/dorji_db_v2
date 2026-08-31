package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class AdminWebOrderDao {

    private final JdbcTemplate jdbcTemplate;

    public AdminWebOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPendingWebOrders() {
        String sql = "SELECT o.Order_ID, o.Customer_ID, o.Style_ID, o.Order_Date, o.Total_Price, " +
                "u.First_name, u.Last_name, " +
                "(SELECT p.Phone_Number FROM USER_PHONE p WHERE p.USER_ID = u.USER_ID LIMIT 1) AS Phone_Number, " +
                "s.Style_name " +
                "FROM OUTFIT_ORDER o " +
                "JOIN APP_USER u ON o.Customer_ID = u.USER_ID " +
                "JOIN STYLE_TEMPLATE s ON o.Style_ID = s.Style_ID " +
                "WHERE o.Order_Status = 'PENDING_APPROVAL' " +
                "ORDER BY o.Order_Date DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getOrderById(int orderId) {
        String sql = "SELECT o.*, u.First_name, u.Last_name, s.Style_name, s.Base_Price, " +
                "(SELECT p.Phone_Number FROM USER_PHONE p WHERE p.USER_ID = u.USER_ID LIMIT 1) AS Phone_Number " +
                "FROM OUTFIT_ORDER o " +
                "JOIN APP_USER u ON o.Customer_ID = u.USER_ID " +
                "JOIN STYLE_TEMPLATE s ON o.Style_ID = s.Style_ID " +
                "WHERE o.Order_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, orderId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> getOrderMeasurements(int orderId) {
        String sql = "SELECT Body_Part, Inch_Value FROM MEASUREMENT WHERE Order_ID = ? ORDER BY Body_Part";
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public List<Map<String, Object>> getOrderFabrics(int orderId) {
        String sql = "SELECT Material_Type, Color, Length_Meters FROM FABRIC_MATERIAL WHERE Order_ID = ?";
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public List<Map<String, Object>> getActiveTailors() {
        String sql = "SELECT u.USER_ID, u.First_name, u.Last_name, t.Specialty " +
                "FROM APP_USER u JOIN TAILOR t ON u.USER_ID = t.USER_ID " +
                "WHERE t.Active_status = 1 ORDER BY u.First_name";
        return jdbcTemplate.queryForList(sql);
    }

    public void approveOrder(int orderId, LocalDate deliveryDate, BigDecimal totalPrice,
                             BigDecimal advancePaid, int tailorId) {
        String sql = "UPDATE OUTFIT_ORDER SET Delivery_Date = ?, Total_Price = ?, Advance_Paid = ?, " +
                "Tailor_ID = ?, Order_Status = 'MEASURED' WHERE Order_ID = ?";
        jdbcTemplate.update(sql, Date.valueOf(deliveryDate), totalPrice, advancePaid, tailorId, orderId);
    }
}
