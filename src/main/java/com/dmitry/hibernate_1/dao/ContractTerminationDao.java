package com.dmitry.hibernate_1.dao;


import com.dmitry.hibernate_1.model.ContractTermination;
import java.time.LocalDate;
import java.util.List;

public interface ContractTerminationDao extends GenericDao<ContractTermination, String> {
    List<ContractTermination> findByTerminationDate(LocalDate date);
}
