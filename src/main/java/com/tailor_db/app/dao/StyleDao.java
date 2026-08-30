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
        String sql = "SELECT s.Style_ID, s.Style_name, s.Base_Price, s.Estimated_Labor_Hours, (SELECT Image_Path FROM STYLE_IMAGE WHERE Style_ID = s.Style_ID LIMIT 1) AS Image_Path FROM STYLE_TEMPLATE s ORDER BY s.Style_name ASC";
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

    public void insert(String name, double price, double hours, String imagePath) {
        String sql = "INSERT INTO STYLE_TEMPLATE (Style_name, Base_Price, Estimated_Labor_Hours, Image_Path) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, name, price, hours, imagePath);
    }

    public void update(int id, String name, double price, double hours) {
        String sql = "UPDATE STYLE_TEMPLATE SET Style_name = ?, Base_Price = ?, Estimated_Labor_Hours = ? WHERE Style_ID = ?";
        jdbcTemplate.update(sql, name, price, hours, id);
    }

    public void delete(int id) {
        String sql = "DELETE FROM STYLE_TEMPLATE WHERE Style_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void insertStyleImage(int styleId, String imagePath) {
        String sql = "INSERT INTO STYLE_IMAGE (Style_ID, Image_Path) VALUES (?, ?)";
        jdbcTemplate.update(sql, styleId, imagePath);
    }

    public void deleteStyleImage(int imageId) {
        String sql = "DELETE FROM STYLE_IMAGE WHERE Image_ID = ?";
        jdbcTemplate.update(sql, imageId);
    }

    public List<Map<String, Object>> findImagesByStyleId(int styleId) {
        String sql = "SELECT * FROM STYLE_IMAGE WHERE Style_ID = ?";
        return jdbcTemplate.queryForList(sql, styleId);
    }

    public Map<String, Object> findImageById(int imageId) {
        String sql = "SELECT * FROM STYLE_IMAGE WHERE Image_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, imageId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}