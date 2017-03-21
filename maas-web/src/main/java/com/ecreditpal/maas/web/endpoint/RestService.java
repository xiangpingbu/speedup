package com.ecreditpal.maas.web.endpoint;


import com.ecreditpal.maas.common.IPBasedRateLimiter;
import com.ecreditpal.maas.common.utils.OkHttpUtil;
import com.ecreditpal.maas.model.bean.XYBModelBean;
import com.ecreditpal.maas.web.bean.User;
import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

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
        } else {
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
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns user details", notes = "Returns a user detail by json", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of user detail", response = User.class),
            @ApiResponse(code = 404, message = "User with given username does not exist"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public XYBModelBean getUser2(@ApiParam(name = "userName", value = "Alphanumeric login to the application", required = true) XYBModelBean bean) {
        log.info("123");
        return bean;
    }

    @POST
    @Path("/xyb")
    @ApiOperation(value = "XYB Model")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = Response.class)})
    public Response getFraudModelScore(
            @ApiParam(value = "creditQueryTimes", required = true) @FormParam("creditQueryTimes") String creditQueryTimes,
            @ApiParam(value = "creditLimit", required = true) @FormParam("creditLimit") String creditLimitList,
            @ApiParam(value = "totalUsedLimit", required = true) @FormParam("totalUsedLimit") String totalUsedLimit,
            @ApiParam(value = "totalCreditLimit", required = true) @FormParam("totalCreditLimit") String totalCreditLimit,
            @ApiParam(value = "personalEducation", required = true) @FormParam("personalEducation") String personalEducation,
            @ApiParam(value = "personalLiveCase", required = true) @FormParam("personalLiveCase") String personalLiveCase,
            @ApiParam(value = "personalLiveJoin", required = true) @FormParam("personalLiveJoin") String personalLiveJoin,
            @ApiParam(value = "personalYearIncome", required = true) @FormParam("personalYearIncome") String personalYearIncome,
            @ApiParam(value = "repaymentFrequency", required = true) @FormParam("repaymentFrequency") String repaymentFrequency,
            @ApiParam(value = "birthday", required = true) @FormParam("birthday") String birthday,
            @ApiParam(value = "loanDay", required = true) @FormParam("loanDay") String loanDay,
            @ApiParam(value = "clientGender", required = true) @FormParam("clientGender") String clientGender
    ) throws Exception {
        Map<String, String> map = new HashMap<String, String>(12);

        map.put("creditQueryTimes", creditQueryTimes);
        map.put("creditLimit", creditLimitList);
        map.put("totalUsedLimit", totalUsedLimit);
        map.put("totalCreditLimit", totalCreditLimit);
        map.put("personalEducation", personalEducation);
        map.put("personalLiveCase", personalLiveCase);
        map.put("personalLiveJoin", personalLiveJoin);
        map.put("personalYearIncome", personalYearIncome);
        map.put("repaymentFrequency", repaymentFrequency);
        map.put("birthday", birthday);
        map.put("loanDay", loanDay);
        map.put("clientGender", clientGender);

        String result = OkHttpUtil.getInstance().doPost("http://dolphin.mycreditpal.com:8888/ecreditpal/rest/model/xyb", map, null);

        return Response.status(Response.Status.OK).entity(result).type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}
