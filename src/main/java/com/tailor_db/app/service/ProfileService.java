package com.tailor_db.app.service;

import com.tailor_db.app.dao.ProfileDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProfileService {

    private final ProfileDao profileDao;
    private final AuditLogService auditLogService;

    public ProfileService(ProfileDao profileDao, AuditLogService auditLogService) {
        this.profileDao = profileDao;
        this.auditLogService = auditLogService;
    }

    public Map<String, Object> getProfile(int userId) {
        return profileDao.getTailorProfile(userId);
    }

    @Transactional
    public void updateProfile(int userId, Map<String, String> formData) {
        String firstName = formData.get("firstName");
        String lastName = formData.get("lastName");
        String specialty = formData.get("specialty");

        profileDao.updateUserProfile(userId, firstName, lastName);
        
        // If specialty is provided, update tailor profile
        if (specialty != null) {
            profileDao.updateTailorProfile(userId, specialty);
        }

        auditLogService.logActivity(userId, "UPDATE", "APP_USER/TAILOR", String.valueOf(userId), null, "Updated Profile");
    }

    public List<Map<String, Object>> getPersonalLogs(int tailorId) {
        return profileDao.getPersonalLogs(tailorId);
    }
}
