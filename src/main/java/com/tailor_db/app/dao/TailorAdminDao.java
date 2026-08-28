package com.tailor_db.app.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TailorAdminDao {

    private final JdbcTemplate jdbcTemplate;

    public TailorAdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPendingTailors() {
        String sql = "SELECT u.USER_ID, u.Username, u.First_name, u.Last_name, t.Specialty FROM APP_USER u JOIN TAILOR t ON u.USER_ID = t.USER_ID WHERE t.Active_status IS NULL OR t.Active_status = 0";
        return jdbcTemplate.queryForList(sql);
    }

    public void approveTailor(int tailorId) {
        String sql = "UPDATE TAILOR SET Active_status = 1 WHERE USER_ID = ?";
        jdbcTemplate.update(sql, tailorId);
    }

    public void deleteTailor(int tailorId) {
        String sql = "DELETE FROM APP_USER WHERE USER_ID = ?";
        jdbcTemplate.update(sql, tailorId);
    }

    public boolean isTailorActive(int tailorId) {
        String sql = "SELECT COALESCE(Active_status, 0) FROM TAILOR WHERE USER_ID = ?";
        try {
            Integer status = jdbcTemplate.queryForObject(sql, Integer.class, tailorId);
            return status != null && status == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
