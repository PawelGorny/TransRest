package com.pawelgorny.transrest.service;

import com.pawelgorny.transrest.model.util.TransactionData;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;


public class TransactionApiTest extends AbstractEntityExampleTest {

    private static final String TRANSACTION_URL = "http://127.0.0.1:9000/api/transaction";

    @Test
    public void testTransaction() throws IOException {
        String transactionId = createTransaction();
        Assert.assertNotNull(transactionId);
        commitTransaction(transactionId);
        transactionId = createTransaction();
        Assert.assertNotNull(transactionId);
        rollbackTransaction(transactionId);
    }

    String createTransaction(){
        Response response = WebClient.create(TRANSACTION_URL, participantProviders)
                .path("/create")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(USER);
        ObjectMapper mapper = new ObjectMapper();
        TransactionData transactionResponse = null;
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        try {
            transactionResponse = mapper.readValue((InputStream) response.getEntity(), TransactionData.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e);
        }
        return transactionResponse.getId();
    }

    void commitTransaction(String key){
        TransactionData transactionData = new TransactionData();
        transactionData.setId(key);
        Response response = WebClient.create(TRANSACTION_URL, participantProviders)
                .path("/commit/")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(transactionData);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    void rollbackTransaction(String key){
        Response response = WebClient.create(TRANSACTION_URL, participantProviders)
                .path("/rollback/"+key)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
