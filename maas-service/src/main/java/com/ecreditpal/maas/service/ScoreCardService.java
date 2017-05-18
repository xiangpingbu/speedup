package com.ecreditpal.maas.service;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.common.utils.ClassUtil;
import com.ecreditpal.maas.model.model.AKDModel;
import com.ecreditpal.maas.model.model.XYBModel;
import com.ecreditpal.maas.service.annotation.Model;
import com.ecreditpal.maas.service.annotation.ModelApi;
import org.apache.avro.generic.GenericRecord;

import javax.xml.bind.JAXBException;
import java.util.List;
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
        lookupEventMessage.getResponseInfo().setResponseBody(score);

        return score;
    }

    @ModelApi(apiCode = "M112")
    public Object akdService(Map<String, String> map, GenericRecord record) {
        AKDModel akdModel = new AKDModel();
        Double score = (Double) akdModel.run(map);

        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = akdModel.ParseVariables(akdModel.getVariableList(), score.toString(), AKDModel.AKDModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);
        lookupEventMessage.getResponseInfo().setResponseBody(String.valueOf(Math.round(score)));

        return Math.round(score);
    }
}
