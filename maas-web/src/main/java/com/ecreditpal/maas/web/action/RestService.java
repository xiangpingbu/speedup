package com.ecreditpal.maas.web.action;


import com.ecreditpal.maas.common.IPBasedRateLimiter;
import com.ecreditpal.maas.web.bean.FileContent;
import com.ecreditpal.maas.web.bean.User;
import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author lifeng
 */
@Slf4j
@Api(value = "users", description = "Endpoint for rest test")
@Path("/restService")
public class RestService {

    @Context
    HttpServletRequest request;

    @GET
    @Path("/getUserText")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserText() {
        IPBasedRateLimiter.getInstance().tryAcquire("");
        return "Hello,World!";
    }

    @GET
    @Path("/getUserJson")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserJson(@QueryParam("ip") String ip) {
        User user = new User();
        if (IPBasedRateLimiter.getInstance().tryAcquire(ip)) {
            user.setName("snail");
            user.setAge("23");
            user.setSex("male");
        }else{
            user.setName("sona");
            user.setAge("23");
            user.setSex("female");
        }
        return user;
    }


    @GET
    @Path("/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns user details", notes = "Returns a user detail by json", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user detail", response = User.class),
            @ApiResponse(code = 404, message = "User with given username does not exist"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public User getUser(@ApiParam(name = "userName", value = "Alphanumeric login to the application", required = true) @PathParam("userName") String userName) {
        User user = new User();
        user.setName("snail");
        user.setAge("22");
        user.setSex("male");
        return user;
    }

    @POST
    @Path("/row")
    @Produces(MediaType.APPLICATION_JSON)
    public FileContent getFromFile(@ApiParam(name = "ip", value = "ip from row", required = true) @FormParam("ip") String ip,
                                   @ApiParam(name = "osType", value = "osType from row", required = true) @FormParam("osType") String osType) {
        FileContent fileContent = new FileContent();
        fileContent.setIp(ip);
        fileContent.setContent(osType);
        log.info("ip,{},osType,{}",ip,osType);
        return fileContent;
    }
}
