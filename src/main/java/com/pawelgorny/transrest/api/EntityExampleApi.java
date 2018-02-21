package com.pawelgorny.transrest.api;

import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.service.EntityExampleService;
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

@Path("/api/entityExample")
@Component
public class EntityExampleApi {

    private static final Logger LOGGER = LogManager.getLogger(EntityExampleApi.class);

    @Autowired
    private EntityExampleService service;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/create")
    public EntityExample create(EntityExample entityExample) {
        return service.create(entityExample);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response find(@PathParam("id") String id) {
        EntityExample result = service.findById(Long.valueOf(id));
        if (result!=null){
            return Response.ok(result).build();
        }
        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
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

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/all")
    public List<EntityExample> findAll() {
        return service.findAll();
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/create/{transactionKey}")
    public Response createInTransaction(@PathParam("transactionKey") String transactionKey,
                                        EntityExample entityExample) {
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

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{transactionKey}/{id}")
    public Response findInTransaction(@PathParam("transactionKey") String transactionKey,
                                           @PathParam("id") String id){
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

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{transactionKey}/{id}")
    public Response deleteInTransaction(@PathParam("transactionKey") String transactionKey,
                                      @PathParam("id") String id){
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

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/all/{transactionKey}")
    public Response findAll(@PathParam("transactionKey") String transactionKey) {
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
