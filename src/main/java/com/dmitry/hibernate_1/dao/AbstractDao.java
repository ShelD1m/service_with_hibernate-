package com.dmitry.hibernate_1.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import com.dmitry.hibernate_1.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public abstract class AbstractDao<T, ID extends Serializable> implements GenericDao<T, ID> {

    private final Class<T> clazz;
    protected final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    protected AbstractDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected void executeInsideTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Transaction failed for " + clazz.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    protected <R> R executeWithResult(Function<Session, R> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            R result = action.apply(session);
            // if (transaction != null) transaction.commit();
            return result;
        } catch (RuntimeException e) {
            // if (transaction != null && transaction.isActive()) transaction.rollback();
            System.err.println("Read operation failed for " + clazz.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void save(T entity) {
        executeInsideTransaction(session -> session.save(entity));
    }

    @Override
    public void update(T entity) {
        Transaction transaction = null;
        T mergedEntity = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            mergedEntity = (T) session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        /*return mergedEntity;*/
    }


    @Override
    public void delete(T entity) {
        executeInsideTransaction(session -> session.delete(entity));
    }

    @Override
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        } else {
            System.out.println(clazz.getSimpleName() + " with ID " + id + " not found for deletion.");
        }
    }

    @Override
    public T findById(ID id) {
        return executeWithResult(session -> session.get(clazz, id));
    }

    @Override
    public List<T> findAll() {
        return executeWithResult(session -> session.createQuery("FROM " + clazz.getName(), clazz).list());
    }
}
