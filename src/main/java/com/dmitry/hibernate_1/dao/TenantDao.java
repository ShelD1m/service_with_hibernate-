package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.Tenant;
public interface TenantDao extends GenericDao<Tenant, Integer> {
    Tenant findByPassport(String passportSerialAndNumber);
}