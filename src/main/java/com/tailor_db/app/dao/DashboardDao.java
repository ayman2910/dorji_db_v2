package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardDao {

    private final JdbcTemplate jdbcTemplate;

    public DashboardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getPendingOrdersCount() {
        String sql = "SELECT COUNT(*) FROM OUTFIT_ORDER WHERE Order_Status != 'DELIVERED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(Total_Price), 0.0) FROM OUTFIT_ORDER";
        Double total = jdbcTemplate.queryForObject(sql, Double.class);
        return total != null ? total : 0.0;
    }

    public int getLowStockItemCount() {
        String sql = "SELECT COUNT(*) FROM INVENTORY_ITEM WHERE Current_Stock_Qty <= Reorder_Level";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
