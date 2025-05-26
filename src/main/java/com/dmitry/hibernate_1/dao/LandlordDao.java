package com.dmitry.hibernate_1.dao;


import com.dmitry.hibernate_1.model.Landlord;
public interface LandlordDao extends GenericDao<Landlord, Integer> {
    Landlord findByPassport(String passportSerialAndNumber);
}