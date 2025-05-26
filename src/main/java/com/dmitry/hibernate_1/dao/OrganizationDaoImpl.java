package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Organization;
import com.dmitry.hibernate_1.util.HibernateUtil;

import java.util.List;

public class OrganizationDaoImpl extends AbstractDao<Organization, Integer> implements OrganizationDao {

    public OrganizationDaoImpl() {
        super(Organization.class);
    }

    @Override
    public Organization findByName(String name) {
        return executeWithResult(session ->
                session.createQuery("FROM Organization o WHERE o.organizationName = :orgName", Organization.class)
                        .setParameter("orgName", name)
                        .uniqueResult()
        );
    }

    @Override
    public void deleteById(Integer id) {
        Organization org = findById(id);
        if (org != null) delete(org);
    }
}
