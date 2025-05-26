package com.dmitry.hibernate_1.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.dmitry.hibernate_1.model.ContractSigning;
import com.dmitry.hibernate_1.model.Tenant;
import com.dmitry.hibernate_1.model.Apartment;
import java.time.LocalDate;
import java.util.List;

public class ContractSigningDaoImpl extends AbstractDao<ContractSigning, String> implements ContractSigningDao {
    public ContractSigningDaoImpl() {
        super(ContractSigning.class);
    }

    @Override
    public List<ContractSigning> findByTenant(Tenant tenant) {
        return executeWithResult(session -> {
            Query<ContractSigning> query = session.createQuery("FROM ContractSigning cs WHERE cs.tenant = :tenantObj", ContractSigning.class);
            query.setParameter("tenantObj", tenant);
            return query.list();
        });
    }

    @Override
    public List<ContractSigning> findByApartment(Apartment apartment) {
        return executeWithResult(session -> {
            Query<ContractSigning> query = session.createQuery("FROM ContractSigning cs WHERE cs.apartment = :apartmentObj", ContractSigning.class);
            query.setParameter("apartmentObj", apartment);
            return query.list();
        });
    }

    @Override
    public List<ContractSigning> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return executeWithResult(session -> {
            Query<ContractSigning> query = session.createQuery("FROM ContractSigning cs WHERE cs.signingDate BETWEEN :startDate AND :endDate", ContractSigning.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.list();
        });
    }
}