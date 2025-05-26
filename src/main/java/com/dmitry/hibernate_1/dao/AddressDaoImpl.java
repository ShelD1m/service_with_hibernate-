package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.Address;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AddressDaoImpl extends AbstractDao<Address, Integer> implements AddressDao {
    public AddressDaoImpl() {
        super(Address.class);
    }

    @Override
    public List<Address> findByCity(String city) {
        try (Session session = sessionFactory.openSession()) {
            Query<Address> query = session.createQuery("FROM Address WHERE cityName = :cityParam", Address.class);
            query.setParameter("cityParam", city);
            return query.list();
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }
}
