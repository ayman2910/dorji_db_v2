package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ProfileDao {

    private final JdbcTemplate jdbcTemplate;

    public ProfileDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateUserProfile(int userId, String firstName, String lastName) {
        String sql = "UPDATE APP_USER SET First_name=?, Last_name=? WHERE USER_ID=?";
        jdbcTemplate.update(sql, firstName, lastName, userId);
    }

    public void updateTailorProfile(int userId, String specialty) {
        String sql = "UPDATE TAILOR SET Specialty=? WHERE USER_ID=?";
        jdbcTemplate.update(sql, specialty, userId);
    }

    public List<Map<String, Object>> getPersonalLogs(int tailorId) {
        String sql = "SELECT * FROM TAILOR_ACTIVITY_LOG WHERE Tailor_ID = ? ORDER BY Log_Timestamp DESC LIMIT 50";
        return jdbcTemplate.queryForList(sql, tailorId);
    }

    public Map<String, Object> getTailorProfile(int userId) {
        String sql = "SELECT u.First_name, u.Last_name, u.Username, u.Role, t.Specialty FROM APP_USER u LEFT JOIN TAILOR t ON u.USER_ID = t.USER_ID WHERE u.USER_ID = ?";
        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
