package com.dmitry.hibernate_1.dao;


import com.dmitry.hibernate_1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Apartment;
import com.dmitry.hibernate_1.model.Landlord;
import java.util.List;


public class ApartmentDaoImpl extends AbstractDao<Apartment, Integer> implements ApartmentDao {
    public ApartmentDaoImpl() {
        super(Apartment.class);
    }

    @Override
    public List<Apartment> findByOwner(Landlord owner) {
        return executeWithResult(session -> {
            Query<Apartment> query = session.createQuery("FROM Apartment a WHERE a.landlordId = :ownerObj", Apartment.class);
            query.setParameter("ownerObj", owner);
            return query.list();
        });
    }

    @Override
    public List<Apartment> findByNumberOfRooms(int roomCount) {
        return executeWithResult(session -> {
            Query<Apartment> query = session.createQuery("FROM Apartment a WHERE a.roomCount = :rooms", Apartment.class);
            query.setParameter("rooms", roomCount);
            return query.list();
        });
    }
    @Override
    public List<Apartment> findByLandlordId(int id){
        Session session = HibernateUtil.getSessionFactory().openSession();
        try{
            Query<Apartment> query = session.createQuery("FROM Apartment a WHERE a.landlordId = :landlordId", Apartment.class);
            query.setParameter("landlordId", id);
            return query.list();
        }finally {
            session.close();
        }
    }
}
