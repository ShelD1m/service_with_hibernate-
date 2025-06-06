package com.dmitry.hibernate_1.dao;

import com.dmitry.hibernate_1.model.ContractSigning;
import com.dmitry.hibernate_1.model.Tenant;
import com.dmitry.hibernate_1.model.Apartment;
import java.time.LocalDate;
import java.util.List;

public interface ContractSigningDao extends GenericDao<ContractSigning, String> {
    List<ContractSigning> findByTenant(Tenant tenant);
    List<ContractSigning> findByApartment(Apartment apartment);
    List<ContractSigning> findByDateRange(LocalDate startDate, LocalDate endDate);
}
