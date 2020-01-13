package com.myth.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@Api(value="")
@Produces(MediaType.APPLICATION_JSON)
public class IndexResource {
    @Inject
    public IndexResource() {
    }

    @GET
    @Timed
    @ApiOperation(
            value = "Get status of server",
            notes = "Returns HTTP OK when this API is called.")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.OK_200, message = "")})
    public Response get() {
        return Response.status(Response.Status.OK).build();
    }
}