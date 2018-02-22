package com.pawelgorny.transrest.api;

import com.pawelgorny.transrest.model.util.TransactionData;
import com.pawelgorny.transrest.service.TransactionService;
import io.swagger.annotations.*;
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

@Api(value = "transaction")
@Path("/api/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class TransactionApi {

    private static final Logger LOGGER = LogManager.getLogger(TransactionApi.class);

    @Autowired
    private TransactionService transactionService;

    @ApiOperation(value = "Creates the transaction", response = TransactionData.class)
    @ApiResponses(value = {
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 201, message = "Success")})
    @PUT
    @Path("/create")
    public Response create(@ApiParam(value = "Requester Id", required = true) String clientId) {
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

    @ApiOperation(value = "Rollbacks the transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 200, message = "Success")})
    @DELETE
    @Path("/rollback/{transactionKey}")
    public Response rollback(@ApiParam(value = "Transaction Id to rollback", required = true) @PathParam("transactionKey") String transactionKey) {
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

    @ApiOperation(value = "Commits the transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 200, message = "Success")})
    @POST
    @Path("/commit/")
    public Response commit(@ApiParam(value = "TransactionData to commit", required = true) TransactionData transactionData) {
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
