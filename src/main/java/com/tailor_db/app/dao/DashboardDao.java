package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DashboardDao {

    private final JdbcTemplate jdbcTemplate;

    public DashboardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getPendingOrdersCount() {
        String sql = "SELECT COUNT(*) FROM OUTFIT_ORDER WHERE Order_Status NOT IN ('DELIVERED', 'CANCELLED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(Total_Price), 0.0) FROM OUTFIT_ORDER WHERE Order_Status != 'CANCELLED'";
        Double total = jdbcTemplate.queryForObject(sql, Double.class);
        return total != null ? total : 0.0;
    }

    public int getLowStockItemCount() {
        String sql = "SELECT COUNT(*) FROM INVENTORY_ITEM WHERE Current_Stock_Qty <= Reorder_Level";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public double getOutstandingBalance() {
        String sql = "SELECT COALESCE(SUM(Total_Price - Advance_Paid), 0.0) FROM OUTFIT_ORDER WHERE Order_Status NOT IN ('DELIVERED', 'CANCELLED')";
        Double total = jdbcTemplate.queryForObject(sql, Double.class);
        return total != null ? total : 0.0;
    }

    public List<Map<String, Object>> getPipelineBreakdown() {
        String sql = "SELECT Order_Status, COUNT(*) as StatusCount FROM OUTFIT_ORDER WHERE Order_Status NOT IN ('DELIVERED', 'CANCELLED') GROUP BY Order_Status";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getUpcomingDeliveries() {
        String sql = "SELECT o.Order_ID, u.First_name, u.Last_name, o.Delivery_Date, o.Order_Status " +
                     "FROM OUTFIT_ORDER o " +
                     "JOIN APP_USER u ON o.Customer_ID = u.USER_ID " +
                     "WHERE o.Order_Status NOT IN ('DELIVERED', 'CANCELLED') " +
                     "ORDER BY o.Delivery_Date ASC " +
                     "LIMIT 5";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getRecentActivity() {
        String sql = "SELECT l.Action_Type, l.Target_Table, l.Old_Value, l.New_Value, l.Log_Timestamp, u.Username AS Tailor_Name " +
                     "FROM TAILOR_ACTIVITY_LOG l " +
                     "JOIN APP_USER u ON l.Tailor_ID = u.USER_ID " +
                     "ORDER BY l.Log_Timestamp DESC " +
                     "LIMIT 5";
        return jdbcTemplate.queryForList(sql);
    }
}
