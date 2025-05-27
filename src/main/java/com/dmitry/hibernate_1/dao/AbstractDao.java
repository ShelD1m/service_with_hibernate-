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
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            session.close();
        }
    }


    protected <R> R executeWithResult(Function<Session, R> action) {
        try (Session session = sessionFactory.openSession()) {
            return action.apply(session);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void save(T entity) {
        executeInsideTransaction(session -> session.save(entity));
    }

    @Override
    public void update(T entity) {
        executeInsideTransaction(session -> session.merge(entity));
    }

    @Override
    public void delete(T entity) {
        executeInsideTransaction(session -> session.delete(entity));
    }

    @Override
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) delete(entity);
    }

    @Override
    public T findById(ID id) {
        return executeWithResult(session -> session.get(clazz, id));
    }

    @Override
    public List<T> findAll() {
        String hql = "FROM " + clazz.getSimpleName();
        return executeWithResult(session -> session.createQuery(hql, clazz).getResultList());
    }

    public List<T> findByField(String fieldName, Object value) {
        String hql = "FROM " + clazz.getSimpleName() + " WHERE " + fieldName + " = :value";
        return executeWithResult(session ->
                session.createQuery(hql, clazz)
                        .setParameter("value", value)
                        .getResultList()
        );
    }
}

