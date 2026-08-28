package com.tailor_db.app.service;

import com.tailor_db.app.dao.TailorAdminDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TailorAdminService {

    private final TailorAdminDao tailorAdminDao;
    private final List<Integer> masterAdmins = List.of(3,6);

    public TailorAdminService(TailorAdminDao tailorAdminDao) {
        this.tailorAdminDao = tailorAdminDao;
    }

    public boolean isMasterAdmin(int userId) {
        return masterAdmins.contains(userId);
    }

    public List<Map<String, Object>> getPending() {
        return tailorAdminDao.getPendingTailors();
    }

    public void approve(int id) {
        tailorAdminDao.approveTailor(id);
    }

    public void deleteTailor(int id) {
        tailorAdminDao.deleteTailor(id);
    }

    public boolean canTailorLogin(int id) {
        return tailorAdminDao.isTailorActive(id);
    }
}
