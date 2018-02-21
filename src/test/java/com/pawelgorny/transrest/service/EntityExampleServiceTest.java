package com.pawelgorny.transrest.service;

import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.model.EntityExampleChild;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.*;
import java.util.List;
import java.util.UUID;


public class EntityExampleServiceTest extends AbstractEntityExampleTest {

    @Autowired
    private TransactionService transactionService;

    private EntityExample getEntityExample(){
        EntityExample entityExample = new EntityExample();
        entityExample.setValue(UUID.randomUUID().toString());

        entityExample.addToChildren(new EntityExampleChild("1_"+entityExample.getValue()));
        entityExample.addToChildren(new EntityExampleChild("2_"+entityExample.getValue()));
        return entityExample;
    }

    @Test
    public void testSimple() {
        EntityExample entityExample = getEntityExample();
        entityExample = service.create(entityExample);
        int chSize = entityExample.getChildren().size();
        Assert.assertNotNull(entityExample.getId());
        entityExample.setValue("Value2");
        entityExample = service.update(entityExample);
        Assert.assertNotNull(entityExample.getId());
        Assert.assertTrue("Value2".equals(entityExample.getValue()));
        List<EntityExample> list = service.findAll();
        Assert.assertEquals(1, list.size());
        entityExample = list.get(0);
        Assert.assertEquals(chSize, entityExample.getChildren().size());
        Assert.assertTrue("Value2".equals(entityExample.getValue()));
        Long id = entityExample.getId();
        entityExample = service.findById(id);
        Assert.assertTrue(id.equals(entityExample.getId()));
        Assert.assertEquals(chSize, entityExample.getChildren().size());
        service.delete(entityExample);
        list = service.findAll();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testTransaction() {
        try {
            String transaction1 = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.createInTransaction(transaction1, entityExample);
            Assert.assertNotNull(entityExample.getId());
            List<EntityExample> list = service.findAll();
            Assert.assertTrue(list.isEmpty());
            list = service.findAllInTransaction(transaction1);
            Assert.assertTrue(!list.isEmpty());
            transactionService.rollback(transaction1);
            list = service.findAll();
            Assert.assertTrue(list.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        try {
            String transaction1 = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.createInTransaction(transaction1, entityExample);
            Assert.assertNotNull(entityExample.getId());
            Long id = entityExample.getId();
            List<EntityExample> list = service.findAll();
            Assert.assertTrue(list.isEmpty());
            list = service.findAllInTransaction(transaction1);
            Assert.assertFalse(list.isEmpty());
            transactionService.commit(transaction1);
            list = service.findAll();
            Assert.assertFalse(list.isEmpty());
            Assert.assertNotNull(service.findById(id));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
    }

    @Test
    public void testTransaction2() {
        try {
            String transaction1 = transactionService.create();
            String transaction2 = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.createInTransaction(transaction1, entityExample);
            Long id = 0L;
            Assert.assertNotNull(entityExample.getId());
            Assert.assertTrue(entityExample.getId() > id);
            id = entityExample.getId();
            List<EntityExample> list = service.findAll();
            Assert.assertTrue(list.isEmpty());
            list = service.findAllInTransaction(transaction1);
            Assert.assertTrue(!list.isEmpty());
            list = service.findAllInTransaction(transaction2);
            Assert.assertTrue(list.isEmpty());
            entityExample = new EntityExample();
            entityExample.setValue("Value");
            entityExample = service.createInTransaction(transaction2, entityExample);
            Assert.assertNotNull(entityExample.getId());
            Assert.assertTrue(entityExample.getId() > id);
            id = entityExample.getId();
            transactionService.commit(transaction2);
            transactionService.rollback(transaction1);
            list = service.findAll();
            Assert.assertFalse(list.isEmpty());
            entityExample = list.get(0);
            Assert.assertEquals(id, entityExample.getId());
            Assert.assertNull(service.findById(1L));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
    }

    @Test
    public void testTransaction3() {
        try {
            String transactionKey = transactionService.create();
            Transaction transaction = transactionService.get(transactionKey);
            Assert.assertNotNull(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        Transaction transaction = transactionService.get("test");
        Assert.assertNull(transaction);
    }

    @Test
    public void testTransaction4DeleteCommit(){
        try {
            String transactionKey = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.create(entityExample);
            List<EntityExample> list = service.findAll();
            Assert.assertFalse(list.isEmpty());
            list = service.findAllInTransaction(transactionKey);
            Assert.assertFalse(list.isEmpty());
            entityExample = service.findByIdInTransaction(transactionKey, entityExample.getId());
            Assert.assertNotNull(entityExample);
            Long id = entityExample.getId();
            service.deleteInTransaction(transactionKey, entityExample);
            entityExample = service.findById(id);
            Assert.assertNotNull(entityExample);
            entityExample = service.findByIdInTransaction(transactionKey, id);
            Assert.assertNull(entityExample);
            transactionService.commit(transactionKey);
            entityExample = service.findById(id);
            Assert.assertNull(entityExample);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
    }

    @Test
    public void testTransaction5UpdateCommit(){
        try {
            String transactionKey = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.create(entityExample);
            Long id = entityExample.getId();
            int ch = entityExample.getChildren().size();
            entityExample.setValue(null);
            entityExample.addToChildren(new EntityExampleChild("X"));
            entityExample = service.updateInTransaction(transactionKey, entityExample);
            Assert.assertNull(entityExample.getValue());
            Assert.assertEquals(ch+1, entityExample.getChildren().size());
            entityExample = service.findById(id);
            Assert.assertNotNull(entityExample.getValue());
            Assert.assertEquals(ch, entityExample.getChildren().size());
            entityExample = service.findByIdInTransaction(transactionKey, id);
            Assert.assertNull(entityExample.getValue());
            transactionService.commit(transactionKey);
            entityExample = service.findById(id);
            Assert.assertNull(entityExample.getValue());
            Assert.assertEquals(ch+1, entityExample.getChildren().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
    }

    @Test
    public void testTransaction6DeleteUpdateRollback(){
        try {
            String transactionKey = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.create(entityExample);
            List<EntityExample> list = service.findAll();
            Assert.assertFalse(list.isEmpty());
            list = service.findAllInTransaction(transactionKey);
            Assert.assertFalse(list.isEmpty());
            entityExample = service.findByIdInTransaction(transactionKey, entityExample.getId());
            Assert.assertNotNull(entityExample);
            Long id = entityExample.getId();
            service.deleteInTransaction(transactionKey, entityExample);
            entityExample = service.findById(id);
            Assert.assertNotNull(entityExample);
            entityExample = service.findByIdInTransaction(transactionKey, id);
            Assert.assertNull(entityExample);
            transactionService.rollback(transactionKey);
            entityExample = service.findById(id);
            Assert.assertNotNull(entityExample);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        try {
            String transactionKey = transactionService.create();
            EntityExample entityExample = getEntityExample();
            entityExample = service.create(entityExample);
            int ch = entityExample.getChildren().size();
            Long id = entityExample.getId();
            entityExample.setValue(null);
            entityExample.addToChildren(new EntityExampleChild("X"));
            entityExample = service.updateInTransaction(transactionKey, entityExample);
            Assert.assertNull(entityExample.getValue());
            Assert.assertEquals(ch+1, entityExample.getChildren().size());
            entityExample = service.findById(id);
            Assert.assertEquals(ch, entityExample.getChildren().size());
            Assert.assertNotNull(entityExample.getValue());
            entityExample = service.findByIdInTransaction(transactionKey, id);
            Assert.assertNull(entityExample.getValue());
            Assert.assertEquals(ch+1, entityExample.getChildren().size());
            transactionService.rollback(transactionKey);
            entityExample = service.findById(id);
            Assert.assertEquals(ch, entityExample.getChildren().size());
            Assert.assertNotNull(entityExample.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
    }


    @Test(expected = InvalidTransactionException.class)
    public void testException1() throws InvalidTransactionException, SystemException {
        transactionService.set("test");
    }

    @Test(expected = InvalidTransactionException.class)
    public void testException2() throws HeuristicRollbackException, RollbackException, InvalidTransactionException, HeuristicMixedException, SystemException {
        transactionService.commit("test");
    }

    @Test(expected = InvalidTransactionException.class)
    public void testException3() throws InvalidTransactionException, SystemException {
        transactionService.rollback("test");
    }

    @Test(expected = InvalidTransactionException.class)
    public void testException4() throws InvalidTransactionException, SystemException {
        transactionService.set((Transaction) null);
    }

    @Test(expected = InvalidTransactionException.class)
    public void testException5() throws HeuristicRollbackException, RollbackException, InvalidTransactionException, HeuristicMixedException, SystemException {
        transactionService.commit((Transaction) null);
    }

    @Test(expected = InvalidTransactionException.class)
    public void testException6() throws InvalidTransactionException, SystemException {
        transactionService.rollback((Transaction) null);
    }
}
