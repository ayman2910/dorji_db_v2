package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class InventoryDao {

    private final JdbcTemplate jdbcTemplate;

    public InventoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM INVENTORY_ITEM ORDER BY Item_Name ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> findById(int id) {
        String sql = "SELECT * FROM INVENTORY_ITEM WHERE Item_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insert(String name, int qty, double cost, int reorderLevel) {
        String sql = "INSERT INTO INVENTORY_ITEM (Item_Name, Current_Stock_Qty, Unit_Cost, Reorder_Level) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, name, qty, cost, reorderLevel);
    }

    public void update(int id, String name, int qty, double cost, int reorderLevel) {
        String sql = "UPDATE INVENTORY_ITEM SET Item_Name = ?, Current_Stock_Qty = ?, Unit_Cost = ?, Reorder_Level = ? WHERE Item_ID = ?";
        jdbcTemplate.update(sql, name, qty, cost, reorderLevel, id);
    }

    public void delete(int id) {
        String sql = "DELETE FROM INVENTORY_ITEM WHERE Item_ID = ?";
        jdbcTemplate.update(sql, id);
    }
}