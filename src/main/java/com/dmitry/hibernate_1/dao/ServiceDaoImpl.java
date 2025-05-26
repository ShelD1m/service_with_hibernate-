package com.dmitry.hibernate_1.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.Service;
import java.math.BigDecimal;
import java.util.List;

public class ServiceDaoImpl extends AbstractDao<Service, Integer> implements ServiceDao {
    public ServiceDaoImpl() {
        super(Service.class);
    }
    @Override
    public List<Service> findByType(String type) {
        return executeWithResult(session -> {
            Query<Service> query = session.createQuery("FROM Service s WHERE s.type = :sType", Service.class);
            query.setParameter("sType", type);
            return query.list();
        });
    }
    @Override
    public List<Service> findByCostLessThan(BigDecimal maxCost) {
        return executeWithResult(session -> {
            Query<Service> query = session.createQuery("FROM Service s WHERE s.cost < :maxCostVal", Service.class);
            query.setParameter("maxCostVal", maxCost);
            return query.list();
        });
    }
}
