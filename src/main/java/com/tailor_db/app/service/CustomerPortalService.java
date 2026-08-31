package com.tailor_db.app.service;

import com.tailor_db.app.dao.CustomerPortalDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CustomerPortalService {

    private final CustomerPortalDao customerPortalDao;

    public CustomerPortalService(CustomerPortalDao customerPortalDao) {
        this.customerPortalDao = customerPortalDao;
    }

    public Map<String, Object> getCustomerProfile(int userId) {
        return customerPortalDao.getCustomerProfile(userId);
    }

    public List<Map<String, Object>> getCustomerOrders(int customerId) {
        return customerPortalDao.getCustomerOrders(customerId);
    }

    public List<Map<String, Object>> getStyleCatalog() {
        return customerPortalDao.getStyleCatalog();
    }

    @Transactional
    public void updateCustomerProfile(int userId, Map<String, String> formData) {
        String firstName = formData.get("firstName");
        String lastName = formData.get("lastName");
        String houseNo = formData.get("houseNo");
        String street = formData.get("street");
        String city = formData.get("city");
        String phone = formData.get("phone");

        customerPortalDao.updateUserNames(userId, firstName, lastName);
        customerPortalDao.updateCustomerAddress(userId, houseNo, street, city);
        customerPortalDao.updatePhone(userId, phone);
    }
}
