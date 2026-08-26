package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StyleDao {

    private final JdbcTemplate jdbcTemplate;

    public StyleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM STYLE_TEMPLATE ORDER BY Style_name ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> findById(int id) {
        String sql = "SELECT * FROM STYLE_TEMPLATE WHERE Style_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insert(String name, double price, double hours) {
        String sql = "INSERT INTO STYLE_TEMPLATE (Style_name, Base_Price, Estimated_Labor_Hours) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, name, price, hours);
    }

    public void update(int id, String name, double price, double hours) {
        String sql = "UPDATE STYLE_TEMPLATE SET Style_name = ?, Base_Price = ?, Estimated_Labor_Hours = ? WHERE Style_ID = ?";
        jdbcTemplate.update(sql, name, price, hours, id);
    }
}