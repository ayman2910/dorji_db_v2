package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuditLogDao {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertLog(int tailorId, String actionType, String targetTable, String recordId, String oldValue, String newValue) {
        String sql = "INSERT INTO TAILOR_ACTIVITY_LOG (Tailor_ID, Action_Type, Target_Table, Record_ID, Old_Value, New_Value) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, tailorId, actionType, targetTable, recordId, oldValue, newValue);
    }

    public List<Map<String, Object>> getRecentLogs() {
        String sql = "SELECT l.*, u.Username AS Tailor_Name FROM TAILOR_ACTIVITY_LOG l JOIN APP_USER u ON l.Tailor_ID = u.USER_ID ORDER BY l.Log_Timestamp DESC LIMIT 100";
        return jdbcTemplate.queryForList(sql);
    }
}
