package com.ecreditpal.maas.web.action;



import com.ecreditpal.maas.web.bean.User;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author lifeng
 */
@Api(value = "users", description = "Endpoint for rest test")
@Path("/restService")
public class RestService {

    @GET
    @Path("/getUserText")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserText() {
        return "Hello,World!";
    }

    @GET
    @Path("/getUserJson")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserJson() {
        User user  = new User();
        user.setName("snail");
        user.setAge("23");
        user.setSex("male");
        return user;
    }


    @GET
    @Path("/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns user details",notes="Returns a user detail by json",response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user detail", response = User.class),
            @ApiResponse(code = 404, message = "User with given username does not exist"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public User getUser(@ApiParam(name = "userName", value = "Alphanumeric login to the application", required = true) @PathParam("userName") String userName) {
        User user  = new User();
        user.setName("snail");
        user.setAge("22");
        user.setSex("male");
        return user;
    }
}
