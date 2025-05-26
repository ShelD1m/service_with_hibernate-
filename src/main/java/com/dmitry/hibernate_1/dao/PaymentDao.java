package com.dmitry.hibernate_1.dao;


import com.dmitry.hibernate_1.model.Payment;
import com.dmitry.hibernate_1.model.Tenant;
import com.dmitry.hibernate_1.model.Service;
import java.time.LocalDate;
import java.util.List;

public interface PaymentDao extends GenericDao<Payment, Integer> {
    List<Payment> findByTenant(Tenant tenant);
    List<Payment> findByService(Service service);
    List<Payment> findByDate(LocalDate date);
}