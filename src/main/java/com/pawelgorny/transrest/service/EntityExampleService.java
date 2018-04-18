package com.pawelgorny.transrest.service;


import com.pawelgorny.transrest.model.EntityExample;

import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import java.util.List;

public interface EntityExampleService {

    EntityExample create(EntityExample entityExample);

    EntityExample update(EntityExample entityExample);

    void delete(EntityExample entityExample);

    EntityExample findById(Long id);

    List<EntityExample> findAll();

    EntityExample createInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException;

    EntityExample updateInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException;

    void deleteInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException;

    EntityExample findByIdInTransaction(String transactionKey, Long id) throws InvalidTransactionException, SystemException;

    List<EntityExample> findAllInTransaction(String transactionKey) throws InvalidTransactionException, SystemException;

    List<EntityExample> searchByQuery(String query);


}
