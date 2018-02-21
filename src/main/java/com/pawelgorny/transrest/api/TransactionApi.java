package com.pawelgorny.transrest.api;

import com.pawelgorny.transrest.model.util.TransactionData;
import com.pawelgorny.transrest.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/api/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class TransactionApi {

    private static final Logger LOGGER = LogManager.getLogger(TransactionApi.class);

    @Autowired
    private TransactionService transactionService;

    @PUT
    @Path("/create")
    public Response create(String clientId) {
        String error;
        try {
            LOGGER.debug("create for "+clientId);
            String result = transactionService.create();
            LOGGER.debug("created for "+ clientId +" " + result);
            TransactionData transactionData = new TransactionData();
            transactionData.setCreated(new Date().getTime());
            transactionData.setId(result);
            return Response.status(Response.Status.CREATED.getStatusCode()).entity(transactionData).build();
        } catch (SystemException | NotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

    @DELETE
    @Path("/rollback/{transactionKey}")
    public Response rollback(@PathParam("transactionKey") String transactionKey) {
        String error;
        try {
            LOGGER.debug("rollback " + transactionKey);
            transactionService.rollback(transactionKey);
            LOGGER.debug("rollback " + transactionKey + " OK");
            return Response.status(Response.Status.OK.getStatusCode()).build();
        } catch (SystemException | InvalidTransactionException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

    @POST
    @Path("/commit/")
    public Response commit(TransactionData transactionData) {
        String error;
        try {
            LOGGER.debug("rollback " + transactionData.getId());
            transactionService.commit(transactionData.getId());
            LOGGER.debug("rollback " + transactionData.getId() + " OK");
            return Response.status(Response.Status.OK.getStatusCode()).build();
        } catch (HeuristicRollbackException | RollbackException | InvalidTransactionException | HeuristicMixedException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }
}
