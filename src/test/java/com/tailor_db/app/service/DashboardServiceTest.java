package com.tailor_db.app.service;

import com.tailor_db.app.dao.DashboardDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardDao dashboardDao;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetDashboardStats() {
        when(dashboardDao.getPendingOrdersCount()).thenReturn(5);
        when(dashboardDao.getTotalRevenue()).thenReturn(15400.50);
        when(dashboardDao.getLowStockItemCount()).thenReturn(2);

        Map<String, Object> stats = dashboardService.getDashboardStats();

        assertEquals(5, stats.get("pendingOrders"));
        assertEquals(15400.50, stats.get("totalRevenue"));
        assertEquals(2, stats.get("lowStockCount"));
    }
}
