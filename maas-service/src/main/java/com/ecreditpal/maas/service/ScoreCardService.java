package com.ecreditpal.maas.service;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.service.model.scorecard.AKDModel;
import com.ecreditpal.maas.service.model.scorecard.XYBModel;
import com.ecreditpal.maas.service.model.scorecard.XYBShenZhenModel;
import com.ecreditpal.maas.service.annotation.Model;
import com.ecreditpal.maas.service.annotation.ModelApi;
import org.apache.avro.generic.GenericRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于评分卡模型的服务
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Model
public class ScoreCardService  {

    /**
     *
     * @param map 模型需求的参数
     * @param record request请求中所有的上下文
     * @return
     */
    @ModelApi(apiCode = "M111")
    public Object xybService(Map<String, String> map, GenericRecord record) {
        XYBModel xybModel = new XYBModel();
        String score = xybModel.run(map).toString();

        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(), score, XYBModel.XYBModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);
//        lookupEventMessage.setResponseInfo(ResponseInfo.newBuilder().setResponseBody(score).build());

        return score;
    }

    @ModelApi(apiCode = "M112")
    public Object akdService(Map<String, String> map, GenericRecord record) {
        AKDModel akdModel = new AKDModel();
        Long score = (Long) akdModel.run(map);
        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = akdModel.ParseVariables(akdModel.getVariableList(), score.toString(), AKDModel.AKDModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);
//        lookupEventMessage.setResponseInfo(ResponseInfo.newBuilder().setResponseBody(score.toString()).build());

        return score;
    }

    @ModelApi(apiCode = "M113")
    public Object xybShenZhenNoGrayService(Map<String, String> map, GenericRecord record) {
        XYBShenZhenModel xybModel = new XYBShenZhenModel();
        Integer score =  (Integer) xybModel.run(map);
        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(), score.toString(), XYBShenZhenModel.modelVariables.getModel()+"_NoGray");
        lookupEventMessage.setModelLog(modelLog);
//        lookupEventMessage.setResponseInfo(ResponseInfo.newBuilder().setResponseBody(score.toString()).build());

        return score;
    }

    @ModelApi(apiCode = "M114")
    public Object xybShenZhenService(Map<String, String> map, GenericRecord record) {
        XYBShenZhenModel xybModel = new XYBShenZhenModel();
        map.put("miguan_cid",map.get("cid"));
        map.put("miguan_name",map.get("name"));
        map.put("miguan_mobile",map.get("mobile"));
        Integer score =  (Integer) xybModel.run(map);
        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(), score.toString(), XYBShenZhenModel.modelVariables.getModel()+"_WithGray");
        lookupEventMessage.setModelLog(modelLog);

        return score;
    }

}
