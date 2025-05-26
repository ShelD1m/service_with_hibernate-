package com.dmitry.hibernate_1.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.ContractTermination;
import java.time.LocalDate;
import java.util.List;

public class ContractTerminationDaoImpl extends AbstractDao<ContractTermination, String> implements ContractTerminationDao {
    public ContractTerminationDaoImpl() {
        super(ContractTermination.class);
    }
    @Override
    public List<ContractTermination> findByTerminationDate(LocalDate date) {
        return executeWithResult(session -> {
            Query<ContractTermination> query = session.createQuery("FROM ContractTermination ct WHERE ct.terminationDate = :tDate", ContractTermination.class);
            query.setParameter("tDate", date);
            return query.list();
        });
    }
}