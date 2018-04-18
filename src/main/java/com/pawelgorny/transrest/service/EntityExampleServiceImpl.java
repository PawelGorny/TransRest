package com.pawelgorny.transrest.service;

import com.github.tennaito.rsql.jpa.JpaCriteriaQueryVisitor;
import com.pawelgorny.transrest.dao.EntityExampleChildDao;
import com.pawelgorny.transrest.dao.EntityExampleDao;
import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.model.util.SearchQueryException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import java.util.Collections;
import java.util.List;

@Service
public class EntityExampleServiceImpl implements EntityExampleService {

    private static final Logger LOGGER = LogManager.getLogger(EntityExampleServiceImpl.class);

    @Autowired
    private EntityExampleDao dao;
    @Autowired
    private EntityExampleChildDao childDao;
    
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private EntityManager entityManager;


    @Override
    @Transactional
    public EntityExample create(EntityExample entityExample) {
        LOGGER.debug("create EntityExample");
        return dao.saveAndFlush(entityExample);
    }

    @Override
    @Transactional
    public EntityExample update(EntityExample entityExample) {
        LOGGER.debug("update EntityExample " + entityExample.getId());
        return dao.saveAndFlush(entityExample);
    }

    @Override
    @Transactional
    public void delete(EntityExample entityExample) {
        LOGGER.debug("delete EntityExample " + entityExample.getId());
        dao.delete(entityExample);
    }

    @Override
    @Transactional
    public EntityExample findById(Long id) {
        LOGGER.debug("findById EntityExample " + id);
        EntityExample result = dao.findOne(id);
        if (result!=null){
            Hibernate.initialize(result.getChildren());
            return result;
        }
        return null;
    }

    @Override
    @Transactional
    public List<EntityExample> findAll() {
        LOGGER.debug("findAll EntityExample ");
        List<EntityExample> result = dao.findAll();
//        for (EntityExample entityExample : result){
//            Hibernate.initialize(entityExample.getChildren());
//        }
        return result;
    }

    @Override
    public EntityExample createInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException {
        transactionService.set(transactionKey);
        LOGGER.debug("saving EntityExample in " + transactionKey);
        EntityExample entityExampleSaved = dao.saveAndFlushNoTransaction(entityExample);
        LOGGER.debug("EntityExample saved " + entityExampleSaved.getId());
        transactionService.free();
        return entityExampleSaved;
    }


    @Override
    public EntityExample updateInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException {
        transactionService.set(transactionKey);
        LOGGER.debug("updating EntityExample in "+transactionKey);
        EntityExample entityExampleSaved = dao.saveAndFlushNoTransaction(entityExample);
        EntityExample x = dao.findOne(entityExample.getId());
        LOGGER.debug("EntityExample updated "+entityExampleSaved.getId());
        transactionService.free();
        return entityExampleSaved;
    }

    @Override
    public void deleteInTransaction(String transactionKey, EntityExample entityExample) throws InvalidTransactionException, SystemException {
        transactionService.set(transactionKey);
        dao.deleteNoTransaction(entityExample);
        transactionService.free();
    }

    @Override
    public EntityExample findByIdInTransaction(String transactionKey, Long id) throws InvalidTransactionException, SystemException {
        transactionService.set(transactionKey);
        EntityExample result = dao.findOne(id);
        transactionService.free();
        return result;
    }

    @Override
    public List<EntityExample> findAllInTransaction(String transactionKey) throws InvalidTransactionException, SystemException {
        transactionService.set(transactionKey);
        LOGGER.debug("findAll EntityExample in " + transactionKey);
        List<EntityExample> result = dao.findAll();
        transactionService.free();
        return result;
    }

    @Override
    public List<EntityExample> searchByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<EntityExample>, EntityManager> visitor = new JpaCriteriaQueryVisitor<>();
        Node rootNode;
        CriteriaQuery<EntityExample> query;
        try {
            rootNode = new RSQLParser().parse(queryString);
            query = rootNode.accept(visitor, entityManager);
        }catch (Exception e){
            LOGGER.trace(e.getMessage(), e);
            throw new SearchQueryException(e.getMessage());
        }
        List<EntityExample> resultList = entityManager.createQuery(query).getResultList();
        if (resultList == null || resultList.isEmpty()){
            return Collections.emptyList();
        }
        return resultList;
    }
}
