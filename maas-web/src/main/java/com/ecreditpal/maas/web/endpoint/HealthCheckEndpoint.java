package com.ecreditpal.maas.web.endpoint;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author lifeng
 * @CreateTime 2017/5/8.
 */
@Api(value = "/status", description = "Endpoint for maas system status")
@Path("/status")
public class HealthCheckEndpoint {
    @GET
    @Path("/health")
    @ApiOperation(value = "Get server health")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Get health status information", response = Response.class)})
    public Response getStatus() throws Exception {
        return Response.status(Response.Status.OK).entity("ok").type(MediaType.TEXT_PLAIN).build();
    }
}
