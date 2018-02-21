package com.pawelgorny.transrest.service;


import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.model.EntityExampleChild;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class EntityExampleApiTest extends TransactionApiTest {

    private static final String BASE_URL = "http://127.0.0.1:9000/api/entityExample";

    @Autowired
    private EntityExampleService service;

    @Test
    public void testCreate() throws IOException {
        String value = UUID.randomUUID().toString();
        EntityExample entityExampleResponse = create(value);

        Assert.assertNotNull(entityExampleResponse.getId());
        Assert.assertEquals(value, entityExampleResponse.getValue());
    }

    @Test
    public void testGetOne() {
        String value = UUID.randomUUID().toString();
        EntityExample entityExample = create(value);
        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        ObjectMapper mapper = new ObjectMapper();
        EntityExample entityExampleResponse = null;
        try {
            entityExampleResponse = mapper.readValue((InputStream) response.getEntity(), EntityExample.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        Assert.assertEquals(entityExampleResponse.getId(), entityExample.getId());
        Assert.assertEquals(entityExampleResponse.getValue(), entityExample.getValue());
    }

    @Test
    public void testGetAllSimple() throws IOException {
        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        List<EntityExample> list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertTrue(list.isEmpty());
        String value = UUID.randomUUID().toString();
        create(value);
        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        list = mapper.readValue((InputStream) response.getEntity(), new TypeReference<List<EntityExample>>() {
        });
        Assert.assertFalse(list.isEmpty());
        EntityExample entityExample = list.get(0);
        Assert.assertNotNull(entityExample.getId());
        Assert.assertEquals(value, entityExample.getValue());
    }

    @Test
    public void testCreateFindInTransactionCommit() throws IOException {
        String transactionId = createTransaction();
        Assert.assertNotNull(transactionId);
        String value = UUID.randomUUID().toString();
        EntityExample entityExample = create(value, transactionId);

        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + transactionId + "/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        ObjectMapper mapper = new ObjectMapper();
        EntityExample entityExampleResponse = null;
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        try {
            entityExampleResponse = mapper.readValue((InputStream) response.getEntity(), EntityExample.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        Assert.assertNotNull(entityExampleResponse);
        Assert.assertEquals(entityExample.getId(), entityExampleResponse.getId());

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + transactionId + "/" + entityExample.getId()+1)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        commitTransaction(transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        mapper = new ObjectMapper();
        entityExampleResponse = null;
        try {
            entityExampleResponse = mapper.readValue((InputStream) response.getEntity(), EntityExample.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        Assert.assertNotNull(entityExampleResponse);
        Assert.assertEquals( entityExample.getId(), entityExampleResponse.getId());
        Assert.assertEquals(entityExample.getValue(), entityExampleResponse.getValue());
    }

    @Test
    public void testCreateFindInTransactionRollback() throws IOException {
        String transactionId = createTransaction();
        Assert.assertNotNull(transactionId);
        String value = UUID.randomUUID().toString();
        EntityExample entityExample = create(value, transactionId);

        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + transactionId + "/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        ObjectMapper mapper = new ObjectMapper();
        EntityExample entityExampleResponse = null;
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        try {
            entityExampleResponse = mapper.readValue((InputStream) response.getEntity(), EntityExample.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        Assert.assertNotNull(entityExampleResponse);
        Assert.assertEquals(entityExample.getId(), entityExampleResponse.getId());

        rollbackTransaction(transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/find/" + entityExample.getId())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateFindAllInTransaction() throws IOException {
        String transactionId = createTransaction();
        Assert.assertNotNull(transactionId);

        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        List<EntityExample> list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertTrue(list.isEmpty());

        create(UUID.randomUUID().toString(), transactionId);
        create(UUID.randomUUID().toString(), transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertTrue(list.isEmpty());

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all/"+transactionId)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertFalse(list.isEmpty());

        int sizeTran = list.size();

        create(UUID.randomUUID().toString());
        create(UUID.randomUUID().toString());

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertFalse(list.isEmpty());

        int size = list.size();

        commitTransaction(transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(size + sizeTran, list.size());
        size = list.size();

        transactionId = createTransaction();
        Assert.assertNotNull(transactionId);

        create(UUID.randomUUID().toString(), transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all/"+transactionId)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(size + 1, list.size());

        rollbackTransaction(transactionId);

        response = WebClient.create(BASE_URL, participantProviders)
                .path("/all")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        mapper = new ObjectMapper();
        list = mapper.readValue((InputStream) response.getEntity(), List.class);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(size, list.size());
    }

    private EntityExample create(String value, String transactionId) {
        EntityExample entityExample = new EntityExample();
        entityExample.setValue(value);

        entityExample.addToChildren(new EntityExampleChild("1_"+entityExample.getValue()));
        entityExample.addToChildren(new EntityExampleChild("2_"+entityExample.getValue()));

        Response response = WebClient.create(BASE_URL, participantProviders)
                .path("/create"+(transactionId!=null?("/"+transactionId) : ""))
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(entityExample);
        ObjectMapper mapper = new ObjectMapper();
        EntityExample entityExampleResponse = null;
        try {
            entityExampleResponse = mapper.readValue((InputStream) response.getEntity(), EntityExample.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        return entityExampleResponse;
    }


    private EntityExample create(String value) {
        return create(value, null);
    }


    @Before
    public void clear() {
        List<EntityExample> list = service.findAll();
        if (!list.isEmpty()) {
            for (EntityExample entityExample : list) {
                service.delete(entityExample);
            }
        }
        list = service.findAll();
        Assert.assertTrue(list.isEmpty());
    }
}
