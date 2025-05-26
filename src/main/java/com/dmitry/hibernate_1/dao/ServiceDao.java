package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.Service;
import java.math.BigDecimal;
import java.util.List;

public interface ServiceDao extends GenericDao<Service, Integer> {
    List<Service> findByType(String type);
    List<Service> findByCostLessThan(BigDecimal maxCost);
}
