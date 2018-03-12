package com.pawelgorny.transrest.api;

import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.service.EntityExampleService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "entityExample")
@Path("/entityExample")
@Component
public class EntityExampleApi {

    private static final Logger LOGGER = LogManager.getLogger(EntityExampleApi.class);

    @Autowired
    private EntityExampleService service;

    @ApiOperation(value = "Creates the entity", response = EntityExample.class)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/create")
    public EntityExample create(@ApiParam(value = "Entity to create", required = true) EntityExample entityExample) {
        return service.create(entityExample);
    }

    @ApiOperation(value = "Finds entity by id",
            response = EntityExample.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Entity not found") })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response find(@ApiParam(value = "Entity Id to find", required = true) @PathParam("id") String id) {
        EntityExample result = service.findById(Long.valueOf(id));
        if (result!=null){
            return Response.ok(result).build();
        }
        return Response.noContent().build();
    }

    @ApiOperation(value = "Deletes entity by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity deleted"),
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 204, message = "Entity not found") })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}")
    public Response delete(@ApiParam(value = "Entity Id to delete", required = true) @PathParam("id") String id) {
        EntityExample result = service.findById(Long.valueOf(id));
        if (result!=null){
            try {
                service.delete(result);
                return Response.ok("SUCCESS").build();
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), e.getMessage()).build();
            }
        }
        return Response.noContent().build();
    }

    @ApiOperation(value = "Finds all the entities",
            response = EntityExample.class,
            responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/all")
    public List<EntityExample> findAll() {
        return service.findAll();
    }


    @ApiOperation(value = "Creates entity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity created"),
            @ApiResponse(code = 503, message = "Error")})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/create/{transactionKey}")
    public Response createInTransaction(@ApiParam(value = "Transaction id", required = true) @PathParam("transactionKey") String transactionKey,
                                        @ApiParam(value = "Entity to create", required = true) EntityExample entityExample) {
        String error;
        try {
            EntityExample result = service.createInTransaction(transactionKey, entityExample);
            return Response.ok(result).build();
        } catch (InvalidTransactionException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

    @ApiOperation(value = "Finds entity by id, in the given transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity found"),
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 204, message = "Entity not found") })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{transactionKey}/{id}")
    public Response findInTransaction(@ApiParam(value = "Transaction id", required = true) @PathParam("transactionKey") String transactionKey,
                                      @ApiParam(value = "Entity Id to find", required = true) @PathParam("id") String id){
        String error;
        try {
            EntityExample result = service.findByIdInTransaction(transactionKey, Long.valueOf(id));
            if (result!=null){
                return Response.ok(result).build();
            }
            return Response.noContent().build();
        }catch (InvalidTransactionException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

    @ApiOperation(value = "Deletes entity by id, in the given transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity deleted"),
            @ApiResponse(code = 503, message = "Error"),
            @ApiResponse(code = 204, message = "Entity not found") })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{transactionKey}/{id}")
    public Response deleteInTransaction(@ApiParam(value = "Transaction id", required = true) @PathParam("transactionKey") String transactionKey,
                                        @ApiParam(value = "Entity Id to delete", required = true) @PathParam("id") String id){
        String error;
        try {
            EntityExample result = service.findByIdInTransaction(transactionKey, Long.valueOf(id));
            if (result!=null){
                service.deleteInTransaction(transactionKey, result);
                return Response.ok(result).build();
            }
            return Response.noContent().build();
        }catch (InvalidTransactionException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

    @ApiOperation(value = "Finds all the entities, in the given transaction",
            response = EntityExample.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 503, message = "Error")})
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/all/{transactionKey}")
    public Response findAll(@ApiParam(value = "Transaction id", required = true) @PathParam("transactionKey") String transactionKey) {
        String error;
        try {
            List<EntityExample> list = service.findAllInTransaction(transactionKey);
            return Response.ok(list).build();
        } catch (InvalidTransactionException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
            error = e.getLocalizedMessage();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), error).build();
    }

}
