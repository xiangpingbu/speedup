package com.ecreditpal.maas.web.endpoint;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.common.utils.json.JsonUtil;
import com.ecreditpal.maas.model.bean.Result;
import com.ecreditpal.maas.service.model.scorecard.AKDModel;
import com.ecreditpal.maas.service.model.scorecard.XYBModel;
import com.ecreditpal.maas.service.model.scorecard.XYBShenZhenModel;
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
@Api(value = "model", description = "Endpoint for model")
@Path("/model")
public class ModelEndpoint {
    @POST
    @Path("/xyb")
    @ApiOperation(value = "XYB Model")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = Response.class)})
    public Response getXybModelScore(
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

        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(), score, XYBModel.XYBModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return Response.status(Response.Status.OK).entity(Result.wrapSuccessfulResult(score)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }


    @POST
    @Path("/xyb/sz/a")
    @ApiOperation(value = "XYB ShenZhen Model A")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = Response.class)})
    public Response getXybShenZhenModelScoreA(
            @ApiParam(value = "degree", required = false) @FormParam("degree") String degree,
            @ApiParam(value = "gender", required = false) @FormParam("gender") String gender,
            @ApiParam(value = "age", required = false) @FormParam("age") String age,
            @ApiParam(value = "cellPhoneAccessTime", required = false) @FormParam("cellPhoneAccessTime") String cellPhoneAccessTime,
            @ApiParam(value = "zhiMaCredit", required = false) @FormParam("zhiMaCredit") String zhiMaCredit,
            @ApiParam(value = "liveCase", required = false) @FormParam("liveCase") String liveCase,
            @ApiParam(value = "pIdCitySameInd", required = false) @FormParam("pIdCitySameInd") String pIdCitySameInd,
            @ApiParam(value = "companyType", required = false) @FormParam("companyType") String companyType,
            @ApiParam(value = "marriageStatus", required = false) @FormParam("marriageStatus") String marriageStatus,
            @Context LookupEventMessage lookupEventMessage
    ) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(12);

        map.put("degree", degree);
        map.put("gender", gender);
        map.put("age", age);
        map.put("cellPhoneAccessTime", cellPhoneAccessTime);
        map.put("zhiMaCredit", zhiMaCredit);
        map.put("liveCase", liveCase);
        map.put("pIdCitySameInd", pIdCitySameInd);
        map.put("companyType", companyType);
        map.put("marriageStatus", marriageStatus);


        XYBShenZhenModel model = new XYBShenZhenModel();
        String score = model.run(map).toString();

        ModelLog modelLog = model.ParseVariables(model.getVariableList(), score, XYBShenZhenModel.modelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return Response.status(Response.Status.OK).entity(Result.wrapSuccessfulResult(score)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/xyb/sz/b")
    @ApiOperation(value = "XYB ShenZhen Model B")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = Response.class)})
    public Response getXybShenZhenModelScoreB(
            @ApiParam(value = "degree", required = false) @FormParam("degree") String degree,
            @ApiParam(value = "gender", required = false) @FormParam("gender") String gender,
            @ApiParam(value = "age", required = false) @FormParam("age") String age,
            @ApiParam(value = "cellPhoneAccessTime", required = false) @FormParam("cellPhoneAccessTime") String cellPhoneAccessTime,
            @ApiParam(value = "zhiMaCredit", required = false) @FormParam("zhiMaCredit") String zhiMaCredit,
            @ApiParam(value = "liveCase", required = false) @FormParam("liveCase") String liveCase,
            @ApiParam(value = "pIdCitySameInd", required = false) @FormParam("pIdCitySameInd") String pIdCitySameInd,
            @ApiParam(value = "companyType", required = false) @FormParam("companyType") String companyType,
            @ApiParam(value = "marriageStatus", required = false) @FormParam("marriageStatus") String marriageStatus,
            @ApiParam(value = "cid", required = false) @FormParam("cid") String cid,
            @ApiParam(value = "name", required = false) @FormParam("name") String name,
            @ApiParam(value = "mobile", required = false) @FormParam("mobile") String mobile,
            @Context LookupEventMessage lookupEventMessage
    ) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(12);

        map.put("degree", degree);
        map.put("gender", gender);
        map.put("age", age);
        map.put("cellPhoneAccessTime", cellPhoneAccessTime);
        map.put("zhiMaCredit", zhiMaCredit);
        map.put("liveCase", liveCase);
        map.put("pIdCitySameInd", pIdCitySameInd);
        map.put("companyType", companyType);
        map.put("marriageStatus", marriageStatus);
        map.put("cid", cid);
        map.put("name", name);
        map.put("mobile", mobile);


        XYBShenZhenModel model = new XYBShenZhenModel();
        String score = model.run(map).toString();

        ModelLog modelLog = model.ParseVariables(model.getVariableList(), score, XYBShenZhenModel.modelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return Response.status(Response.Status.OK).entity(Result.wrapSuccessfulResult(score)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }


    @POST
    @Path("/akd")
    @ApiOperation(value = "AKD Model")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "AKD Model Response", response = Response.class)})
    public Response getAkdModelScore(
            @ApiParam(value = "degree", required = false) @FormParam("degree") String degree,
            @ApiParam(value = "cellPhoneAccessTime", required = false) @FormParam("cellPhoneAccessTime") String cellPhoneAccessTime,
            @ApiParam(value = "contactsClass1Cnt", required = false) @FormParam("contactsClass1Cnt") String contactsClass1Cnt,
            @ApiParam(value = "gender", required = false) @FormParam("gender") String gender,
            @ApiParam(value = "companyType", required = false) @FormParam("companyType") String companyType,
            @ApiParam(value = "age", required = false) @FormParam("age") String age,
            @ApiParam(value = "netFlow", required = false) @FormParam("netFlow") String netFlow,
            @ApiParam(value = "marriageStatus", required = false) @FormParam("marriageStatus") String marriageStatus,
            @Context LookupEventMessage lookupEventMessage
    ) throws Exception {
        Map<String,String> map = JsonUtil.json2Map(lookupEventMessage.getRequestInfo().getFormParams().toString());

//        map.put("account", "xinyongbao");
//        map.put("password", "eHliMjAxNzA0MDc=");

        AKDModel akdModel = new AKDModel();
        String score = akdModel.run(map).toString();

        ModelLog modelLog = akdModel.ParseVariables(akdModel.getVariableList(), score, AKDModel.AKDModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return Response.status(Response.Status.OK).entity(Result.wrapSuccessfulResult(score)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
