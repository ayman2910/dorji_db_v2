package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findByRole(String role) {
        String sql = "SELECT USER_ID, Username FROM APP_USER WHERE Role = ? ORDER BY Username";
        return jdbcTemplate.queryForList(sql, role);
    }

    public Map<String, Object> findByUsername(String username) {
        String sql = "SELECT USER_ID, Username, Role FROM APP_USER WHERE Username = ?";
        try {
            return jdbcTemplate.queryForMap(sql, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
