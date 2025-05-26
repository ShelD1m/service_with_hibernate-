package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.query.Query;
import  com.dmitry.hibernate_1.model.Landlord;
public class LandlordDaoImpl extends AbstractDao<Landlord, Integer> implements LandlordDao {
    public LandlordDaoImpl() {
        super(Landlord.class);
    }
    @Override
    public Landlord findByPassport(String passportSerialAndNumber) {
        return executeWithResult(session -> {
            Query<Landlord> query = session.createQuery("FROM Landlord l WHERE l.passportNumber = :passport", Landlord.class);
            query.setParameter("passport", passportSerialAndNumber);
            return query.uniqueResultOptional().orElse(null);
        });
    }
}
