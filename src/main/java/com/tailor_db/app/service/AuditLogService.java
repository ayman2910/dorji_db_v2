package com.tailor_db.app.service;

import com.tailor_db.app.dao.AuditLogDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuditLogService {

    private final AuditLogDao auditLogDao;

    public AuditLogService(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    public void logActivity(int tailorId, String actionType, String targetTable, String recordId, String oldValue, String newValue) {
        auditLogDao.insertLog(tailorId, actionType, targetTable, recordId, oldValue, newValue);
    }

    public List<Map<String, Object>> getLogs() {
        return auditLogDao.getRecentLogs();
    }
}
