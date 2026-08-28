package com.tailor_db.app.service;

import com.tailor_db.app.dao.DashboardDao;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final DashboardDao dashboardDao;

    public DashboardService(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingOrders", dashboardDao.getPendingOrdersCount());
        stats.put("totalRevenue", dashboardDao.getTotalRevenue());
        stats.put("lowStockCount", dashboardDao.getLowStockItemCount());
        stats.put("outstandingBalance", dashboardDao.getOutstandingBalance());
        stats.put("pipelineBreakdown", dashboardDao.getPipelineBreakdown());
        stats.put("upcomingDeliveries", dashboardDao.getUpcomingDeliveries());
        stats.put("recentActivity", dashboardDao.getRecentActivity());
        return stats;
    }
}
