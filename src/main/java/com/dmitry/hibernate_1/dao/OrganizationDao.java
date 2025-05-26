package com.dmitry.hibernate_1.dao;


import com.dmitry.hibernate_1   .model.Organization;

public interface OrganizationDao extends GenericDao<Organization, Integer> {
    Organization findByName(String name);
}
