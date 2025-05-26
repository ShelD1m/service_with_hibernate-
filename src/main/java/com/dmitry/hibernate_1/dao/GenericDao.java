package com.dmitry.hibernate_1.dao;


import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, ID extends Serializable> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);

    void deleteById(ID id);

    T findById(ID id);
    List<T> findAll();
}
