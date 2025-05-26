package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Payment;
import com.dmitry.hibernate_1.model.Tenant;
import com.dmitry.hibernate_1.model.Service;
import java.time.LocalDate;
import java.util.List;

public class PaymentDaoImpl extends AbstractDao<Payment, Integer> implements PaymentDao {
    public PaymentDaoImpl() {
        super(Payment.class);
    }
    @Override
    public List<Payment> findByTenant(Tenant tenant) {
        return executeWithResult(session -> {
            Query<Payment> query = session.createQuery("FROM Payment p WHERE p.tenant = :tenantObj", Payment.class);
            query.setParameter("tenantObj", tenant);
            return query.list();
        });
    }
    @Override
    public List<Payment> findByService(Service service) {
        return executeWithResult(session -> {
            Query<Payment> query = session.createQuery("FROM Payment p WHERE p.service = :serviceObj", Payment.class);
            query.setParameter("serviceObj", service);
            return query.list();
        });
    }
    @Override
    public List<Payment> findByDate(LocalDate date) {
        return executeWithResult(session -> {
            Query<Payment> query = session.createQuery("FROM Payment p WHERE p.date = :pDate", Payment.class);
            query.setParameter("pDate", date);
            return query.list();
        });
    }
}
