package com.tailor_db.app.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(Map<String, String> formData) {
        String hashed = passwordEncoder.encode(formData.get("password"));

        // 1. Insert into APP_USER (Supertype)
        String userSql = "INSERT INTO APP_USER (Username, Password_hash, First_name, Last_name, Role) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, formData.get("username"));
            ps.setString(2, hashed);
            ps.setString(3, formData.get("firstName"));
            ps.setString(4, formData.get("lastName"));
            ps.setString(5, formData.get("role"));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to retrieve generated USER_ID");
        }
        int newUserId = key.intValue();

        // 2. Insert into USER_PHONE (Multi-valued attribute)
        String phone = formData.get("phone");
        if (phone != null && !phone.trim().isEmpty()) {
            String phoneSql = "INSERT INTO USER_PHONE (USER_ID, Phone_Number) VALUES (?, ?)";
            jdbcTemplate.update(phoneSql, newUserId, phone);
        }

        // 3. Subtype routing
        String role = formData.get("role");
        if ("TAILOR".equalsIgnoreCase(role)) {
            String tailorSql = "INSERT INTO TAILOR (USER_ID, Specialty) VALUES (?, ?)";
            jdbcTemplate.update(tailorSql, newUserId, formData.get("specialty"));
        } else if ("CUSTOMER".equalsIgnoreCase(role)) {
            String customerSql = "INSERT INTO CUSTOMER (USER_ID, House_no, Street, City) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(customerSql, newUserId, formData.get("houseNo"), formData.get("street"), formData.get("city"));
        }
    }
}
