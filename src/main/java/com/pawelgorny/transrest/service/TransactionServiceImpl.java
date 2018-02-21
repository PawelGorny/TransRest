package com.pawelgorny.transrest.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LogManager.getLogger(TransactionServiceImpl.class);

    private Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();

    @Autowired
    private TransactionManager transactionManager;

    @Override
    public String create() throws SystemException, NotSupportedException {
        transactionManager.begin();
        LOGGER.debug("Transaction started " + transactionManager.getTransaction().toString());
        transactionMap.put(transactionManager.getTransaction().toString(), transactionManager.getTransaction());
        Transaction transaction = transactionManager.suspend();
        return transaction.toString();
    }

    @Override
    public void rollback(Transaction transaction) throws InvalidTransactionException, SystemException {
        transactionManager.resume(transaction);
        transactionManager.rollback();
        transactionMap.remove(transaction.toString());
    }

    @Override
    public void rollback(String transactionKey) throws InvalidTransactionException, SystemException {
        Transaction transaction = get(transactionKey);
        if (null != transaction) {
            rollback(transaction);
        } else {
            throw new InvalidTransactionException(transactionKey + " not found");
        }
    }

    @Override
    public void commit(Transaction transaction) throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, InvalidTransactionException {
        if (null == transaction) {
            throw new InvalidTransactionException("null");
        }
        transactionManager.resume(transaction);
        transactionManager.commit();
        transactionMap.remove(transaction.toString());
    }

    @Override
    public void commit(String transactionKey) throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, InvalidTransactionException {
        Transaction transaction = get(transactionKey);
        if (null != transaction) {
            commit(transaction);
        } else {
            throw new InvalidTransactionException(transactionKey + " not found");
        }
    }

    @Override
    public String free() throws SystemException {
        LOGGER.debug("suspending transaction " + transactionManager.getTransaction().toString());
        return transactionManager.suspend().toString();
    }

    @Override
    public void set(String transactionKey) throws InvalidTransactionException, SystemException {
        Transaction transaction = get(transactionKey);
        if (null != transaction) {
            set(transaction);
        } else {
            throw new InvalidTransactionException(transactionKey + " not found");
        }
    }

    @Override
    public void set(Transaction transaction) throws InvalidTransactionException, SystemException {
        if (null != transaction) {
            LOGGER.debug("restoring transaction " + transaction.toString());
            transactionManager.resume(transaction);
        } else {
            throw new InvalidTransactionException("null");
        }
    }

    @Override
    public Transaction get(String key) {
        return transactionMap.get(key);
    }
}
