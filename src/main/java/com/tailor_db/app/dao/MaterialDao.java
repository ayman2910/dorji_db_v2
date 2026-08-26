package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MaterialDao {

    private final JdbcTemplate jdbcTemplate;

    public MaterialDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getFabricsForOrder(int orderId) {
        String sql = "SELECT * FROM FABRIC_MATERIAL WHERE Order_ID = ?";
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public void addFabric(int orderId, String type, String color, double length) {
        String sql = "INSERT INTO FABRIC_MATERIAL (Order_ID, Material_Type, Color, Length_Meters) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, orderId, type, color, length);
    }

    public List<Map<String, Object>> getConsumedInventoryForOrder(int orderId) {
        String sql = "SELECT oci.Item_ID, oci.Quantity_Used, i.Item_Name FROM ORDER_CONSUMES_INVENTORY oci JOIN INVENTORY_ITEM i ON oci.Item_ID = i.Item_ID WHERE oci.Order_ID = ?";
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public void recordInventoryConsumption(int orderId, int itemId, int qty) {
        String sql = "INSERT INTO ORDER_CONSUMES_INVENTORY (Order_ID, Item_ID, Quantity_Used) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, orderId, itemId, qty);
    }

    public void decrementInventoryStock(int itemId, int qty) {
        String sql = "UPDATE INVENTORY_ITEM SET Current_Stock_Qty = Current_Stock_Qty - ? WHERE Item_ID = ?";
        jdbcTemplate.update(sql, qty, itemId);
    }
}
