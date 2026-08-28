package com.tailor_db.app.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DashboardDaoTest {

    @Autowired
    private DashboardDao dashboardDao;

    @Test
    void testGetPendingOrdersCount() {
        int count = dashboardDao.getPendingOrdersCount();
        assertTrue(count >= 0, "Pending orders count should be non-negative");
    }

    @Test
    void testGetTotalRevenue() {
        double revenue = dashboardDao.getTotalRevenue();
        assertTrue(revenue >= 0.0, "Total revenue should be non-negative");
    }

    @Test
    void testGetLowStockItemCount() {
        int lowStockCount = dashboardDao.getLowStockItemCount();
        assertTrue(lowStockCount >= 0, "Low stock count should be non-negative");
    }
}
