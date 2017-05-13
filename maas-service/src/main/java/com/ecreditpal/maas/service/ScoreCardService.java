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
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Model
public class ScoreCardService  {

    @ModelApi(apiCode = "M111")
    public Object xybService(Map<String, String> map, GenericRecord record) {
        XYBModel xybModel = new XYBModel();
        String score = xybModel.run(map).toString();

        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = xybModel.ParseVariables(xybModel.getVariableList(), score, XYBModel.XYBModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return score;
    }

    @ModelApi(apiCode = "M112")
    public Object akdService(Map<String, String> map, GenericRecord record) {
        AKDModel akdModel = new AKDModel();
        String score = akdModel.run(map).toString();

        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = akdModel.ParseVariables(akdModel.getVariableList(), score, AKDModel.AKDModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return score;
    }
}
