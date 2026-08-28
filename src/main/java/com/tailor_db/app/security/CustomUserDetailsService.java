package com.tailor_db.app.security;

import com.tailor_db.app.service.TailorAdminService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;
    private final TailorAdminService tailorAdminService;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate, TailorAdminService tailorAdminService) {
        this.jdbcTemplate = jdbcTemplate;
        this.tailorAdminService = tailorAdminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT USER_ID, Username, Password_hash, First_name, Last_name, Role FROM APP_USER WHERE Username = ?";
        
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Map<String, Object> userData = users.get(0);
        int userId = ((Number) userData.get("USER_ID")).intValue();
        String passwordHash = (String) userData.get("Password_hash");
        String role = (String) userData.get("Role");

        boolean enabled = true;
        if ("TAILOR".equalsIgnoreCase(role)) {
            enabled = tailorAdminService.canTailorLogin(userId);
        }

        return User.withUsername(username)
                .password(passwordHash)
                .authorities("ROLE_" + role)
                .disabled(!enabled)
                .build();
    }
}
