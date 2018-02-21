package com.pawelgorny.transrest.service;

import javax.transaction.*;

public interface TransactionService {

    String create() throws SystemException, NotSupportedException;

    void rollback(Transaction transaction) throws InvalidTransactionException, SystemException;

    void rollback(String transactionKey) throws InvalidTransactionException, SystemException;

    void commit(Transaction transaction) throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, InvalidTransactionException;

    void commit(String transactionKey) throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, InvalidTransactionException;

    String free() throws SystemException;

    void set(Transaction transaction) throws InvalidTransactionException, SystemException;

    void set(String transactionKey) throws InvalidTransactionException, SystemException;


    Transaction get(String key);
}
