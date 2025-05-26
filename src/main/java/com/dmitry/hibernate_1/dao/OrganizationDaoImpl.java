package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Organization;
import com.dmitry.hibernate_1.util.HibernateUtil;

import java.util.List;

public class OrganizationDaoImpl implements OrganizationDao {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public void save(Organization entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Organization entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Organization entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public Organization findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Organization.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Organization> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Organization", Organization.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Organization findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Organization> query = session.createQuery("FROM Organization WHERE organizationName = :orgName", Organization.class);
            query.setParameter("orgName", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
