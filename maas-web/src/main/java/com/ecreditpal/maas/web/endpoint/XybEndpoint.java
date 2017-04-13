package com.ecreditpal.maas.web.endpoint;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.model.bean.Result;
import com.ecreditpal.maas.model.model.XYBModel;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/12.
 */
@Api(value = "xyb", description = "Endpoint for xyb")
@Path("/model")
public class XybEndpoint {
    @POST
    @Path("/xyb")
    @ApiOperation(value = "XYB Model")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = Response.class)})
    public Response getFraudModelScore(
            @ApiParam(value = "creditQueryTimes", required = false) @FormParam("creditQueryTimes") String creditQueryTimes,
            @ApiParam(value = "creditLimit", required = false) @FormParam("creditLimit") String creditLimitList,
            @ApiParam(value = "totalUsedLimit", required = false) @FormParam("totalUsedLimit") String totalUsedLimit,
            @ApiParam(value = "totalCreditLimit", required = false) @FormParam("totalCreditLimit") String totalCreditLimit,
            @ApiParam(value = "personalEducation", required = false) @FormParam("personalEducation") String personalEducation,
            @ApiParam(value = "personalLiveCase", required = false) @FormParam("personalLiveCase") String personalLiveCase,
            @ApiParam(value = "personalLiveJoin", required = false) @FormParam("personalLiveJoin") String personalLiveJoin,
            @ApiParam(value = "personalYearIncome", required = false) @FormParam("personalYearIncome") String personalYearIncome,
            @ApiParam(value = "repaymentFrequency", required = false) @FormParam("repaymentFrequency") String repaymentFrequency,
            @ApiParam(value = "birthday", required = false) @FormParam("birthday") String birthday,
            @ApiParam(value = "loanDay", required = false) @FormParam("loanDay") String loanDay,
            @ApiParam(value = "clientGender", required = false) @FormParam("clientGender") String clientGender,
            @Context LookupEventMessage lookupEventMessage
            ) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(12);

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
        map.put("account", "xinyongbao");
        map.put("password", "eHliMjAxNzA0MDc=");

        XYBModel xybModel = new XYBModel();
        String score = xybModel.run(map).toString();

        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(),score);
        lookupEventMessage.setModelLog(modelLog);



        return Response.status(Response.Status.OK).entity(Result.wrapSuccessfulResult(score)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
