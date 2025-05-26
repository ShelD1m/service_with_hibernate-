package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Tenant;
public class TenantDaoImpl extends AbstractDao<Tenant, Integer> implements TenantDao {
    public TenantDaoImpl() {
        super(Tenant.class);
    }
    @Override
    public Tenant findByPassport(String passportSerialAndNumber) {
        return executeWithResult(session -> {
            Query<Tenant> query = session.createQuery("FROM Tenant t WHERE t.passportNumber = :passport", Tenant.class);
            query.setParameter("passport", passportSerialAndNumber);
            return query.uniqueResultOptional().orElse(null);
        });
    }
}