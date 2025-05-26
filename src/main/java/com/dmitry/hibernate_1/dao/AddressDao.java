package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.Address;

import java.util.List;

public interface AddressDao extends GenericDao<Address, Integer> {
    public List<Address> findByCity(String city);
}
