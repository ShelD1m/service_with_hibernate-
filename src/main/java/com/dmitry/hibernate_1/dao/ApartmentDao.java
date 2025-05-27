package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.Apartment;
import com.dmitry.hibernate_1.model.Apartment;
import com.dmitry.hibernate_1.model.Landlord;
import java.util.List;

public interface ApartmentDao extends GenericDao<Apartment, Integer> {
    List<Apartment> findByOwner(Landlord owner);
    List<Apartment> findByNumberOfRooms(int roomCount);
    List<Apartment> findByLandlordId(int landlordId);
}