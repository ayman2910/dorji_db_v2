package com.tailor_db.app.service;

import com.tailor_db.app.dao.CustomerManagementDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CustomerManagementService {

    private final CustomerManagementDao customerManagementDao;
    private final PasswordEncoder passwordEncoder;

    public CustomerManagementService(CustomerManagementDao customerManagementDao, PasswordEncoder passwordEncoder) {
        this.customerManagementDao = customerManagementDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Map<String, Object>> getAllCustomers() {
        return customerManagementDao.getAllCustomers();
    }

    @Transactional
    public void registerWalkInCustomer(Map<String, String> formData) {
        String firstName = formData.get("firstName");
        String lastName = formData.get("lastName");
        String username = formData.get("username");
        String phone = formData.get("phone");
        String house = formData.get("house");
        String street = formData.get("street");
        String city = formData.get("city");

        String defaultHash = passwordEncoder.encode("walkin123");

        int userId = customerManagementDao.insertAppUser(username, defaultHash, firstName, lastName);

        customerManagementDao.insertCustomerDetails(userId, house, street, city);

        if (phone != null && !phone.trim().isEmpty()) {
            customerManagementDao.insertUserPhone(userId, phone.trim());
        }
    }
}
