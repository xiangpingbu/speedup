package com.ecreditpal.maas.service;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.model.model.AKDModel;
import com.ecreditpal.maas.model.model.XYBModel;
import com.ecreditpal.maas.service.annotation.Model;
import org.apache.avro.generic.GenericRecord;

import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Model(apiCode = "M112")
public class AkdService implements ModelService {

    @Override
    public Object getResult(Map<String, String> map, GenericRecord record) {
        AKDModel akdModel = new AKDModel();
        String score = akdModel.run(map).toString();

        LookupEventMessage lookupEventMessage = (LookupEventMessage)record;
        ModelLog modelLog = akdModel.ParseVariables(akdModel.getVariableList(), score, AKDModel.AKDModelVariables.getModel());
        lookupEventMessage.setModelLog(modelLog);

        return score;
    }
}
