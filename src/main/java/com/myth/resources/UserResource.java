package com.myth.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.myth.context.ServerContext;
import com.myth.models.User;
import com.myth.service.IUserService;
import io.swagger.annotations.*;
import org.eclipse.jetty.http.HttpStatus;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/user")
@Api(value="/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private IUserService userService;

    @Inject
    public UserResource(IUserService userService) {
        this.userService = userService;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Creates a new user",
            notes = "Creates a new user if valid object received.")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.CREATED_201, message = "Created", response = User.class),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "user is either null or exist"),})
    public CompletionStage<Response> create(@ApiParam(value = "payload", required = true) final User user) {
        if (user == null || user.getUserId() > 0) {
            return CompletableFuture
                    .completedFuture(Response.status(Response.Status.BAD_REQUEST).build());
        }
        return CompletableFuture
                .supplyAsync(() -> {
                    User createdUser = userService.createUser(user);
                    return Response.status(HttpStatus.CREATED_201).entity(createdUser.toJson()).build();
                }, ServerContext.getMysqlReadWriteExecutor());
    }

    @GET
    @Path("/{userId}")
    @Timed
    @ApiOperation(
            value = "Get user by id",
            notes = "Returns user by Id. If it does not exist it will return a HTTP 404")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.OK_200, message = "", response = User.class),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "userId is invalid"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Not Found")})
    public CompletionStage<Response> getUser(@ApiParam(value = "userId", example = "1") @PathParam("userId") long userId) {
        if (userId < 1) {
            return CompletableFuture
                    .completedFuture(Response.status(Response.Status.BAD_REQUEST).build());
        }
        return CompletableFuture
                .supplyAsync(() -> {
                    User user = userService.getUser(userId);
                    if (user == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    return Response.ok(user.toJson()).build();
                }, ServerContext.getMysqlReadWriteExecutor());
    }

    @PUT
    @Path("{userId}")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Updates user by id",
            notes = "Updates a user if available in the database")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.OK_200, message = "Updated"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Not Found")})
    public CompletionStage<Response> update(@ApiParam(value = "userId", example = "1") @PathParam("userId") long userId,
                           @ApiParam(value = "payload", required = true) User user) {
        if (user == null || user.getUserId() < 1) {
            return CompletableFuture
                    .completedFuture(Response.status(Response.Status.BAD_REQUEST).build());
        }
        return CompletableFuture
                .supplyAsync(() -> {
                    User createdUser = userService.updateUser(user);
                    return Response.status(HttpStatus.OK_200).entity(createdUser.toJson()).build();
                }, ServerContext.getMysqlReadWriteExecutor());
    }

    @DELETE
    @Path("/{userId}")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Deletes user by id",
            notes = "Deletes a if available in the database")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NO_CONTENT_204, message = "Deleted"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Not Found")})

    public CompletionStage<Response> delete(@ApiParam(value = "userId", example = "1") @PathParam("userId") long userId) {
        if (userId < 1) {
            return CompletableFuture
                    .completedFuture(Response.status(Response.Status.BAD_REQUEST).build());
        }
        return CompletableFuture
                .supplyAsync(() -> {
                    boolean isDeleted = userService.deleteUser(userId);
                    if (!isDeleted) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    return Response.noContent().build();
                }, ServerContext.getMysqlReadWriteExecutor());
    }
}
