package com.tailor_db.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerManagementDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerManagementDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertAppUser(String username, String hash, String firstName, String lastName) {
        String sql = "INSERT INTO APP_USER (Username, Password_hash, First_name, Last_name, Role) VALUES (?, ?, ?, ?, 'CUSTOMER')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated USER_ID");
        }
        return key.intValue();
    }

    public void insertCustomerDetails(int userId, String house, String street, String city) {
        String sql = "INSERT INTO CUSTOMER (USER_ID, House_no, Street, City) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, house, street, city);
    }

    public void insertUserPhone(int userId, String phone) {
        String sql = "INSERT INTO USER_PHONE (USER_ID, Phone_Number) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, phone);
    }

    public List<Map<String, Object>> getAllCustomers() {
        String sql = "SELECT u.USER_ID, u.First_name, u.Last_name, u.Username, p.Phone_Number, c.City " +
                     "FROM APP_USER u " +
                     "JOIN CUSTOMER c ON u.USER_ID = c.USER_ID " +
                     "LEFT JOIN USER_PHONE p ON u.USER_ID = p.USER_ID " +
                     "WHERE u.Role = 'CUSTOMER' " +
                     "ORDER BY u.First_name";
        return jdbcTemplate.queryForList(sql);
    }
}
