package com.tailor_db.app.controller;

import com.tailor_db.app.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private Principal principal;

    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(dashboardService);
    }

    @Test
    void testLoginRoute() {
        assertEquals("login", authController.login());
    }

    @Test
    void testRootRoute() {
        assertEquals("redirect:/dashboard", authController.root());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDashboardAsTailor() {
        when(principal.getName()).thenReturn("tailor_john");
        doReturn((List) List.of(new SimpleGrantedAuthority("ROLE_TAILOR"))).when(authentication).getAuthorities();

        Map<String, Object> stats = Map.of(
                "pendingOrders", 4,
                "totalRevenue", 12000.0,
                "lowStockCount", 1
        );
        when(dashboardService.getDashboardStats()).thenReturn(stats);

        Model model = new ConcurrentModel();
        String viewName = authController.dashboard(principal, authentication, model);

        assertEquals("dashboard", viewName);
        assertEquals("tailor_john", model.getAttribute("username"));
        assertEquals(true, model.getAttribute("isTailor"));
        assertEquals(stats, model.getAttribute("stats"));
        verify(dashboardService, times(1)).getDashboardStats();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDashboardAsCustomer() {
        when(principal.getName()).thenReturn("customer_jane");
        doReturn((List) List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(authentication).getAuthorities();

        Model model = new ConcurrentModel();
        String viewName = authController.dashboard(principal, authentication, model);

        assertEquals("dashboard", viewName);
        assertEquals("customer_jane", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isTailor"));
        assertNull(model.getAttribute("stats"));
        verifyNoInteractions(dashboardService);
    }
}
