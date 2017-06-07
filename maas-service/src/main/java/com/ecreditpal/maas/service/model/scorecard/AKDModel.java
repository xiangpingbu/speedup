package com.ecreditpal.maas.service.model.scorecard;


import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.service.model.ModelNew;
import com.ecreditpal.maas.service.model.variables.Variable;
import com.ecreditpal.maas.service.model.variables.VariableConfiguration;
import com.ecreditpal.maas.service.model.variables.VariableContentHandler;
import lombok.Getter;
import lombok.Setter;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xibu
 * @version 1.0 on 2017/05/10.
 */
@Getter
@Setter
public class AKDModel extends ModelNew {
    private final static Logger logger = LoggerFactory.getLogger(AKDModel.class);
    public static String localVariablePath = ConfigurationManager.getConfiguration().getString("akd_model_variables.xml");
    public static VariableConfiguration AKDModelVariables;
    private static String resultFieldName = "RawResult";
    private static Double alignOffset = 483.9035953;
    private static Double alignFactor = 72.13475204;


    public static PMML pmml;
    public static Evaluator evaluator;

    /**
     * init required data structures
     * init variables and put variable into variable list
     * and variable map
     */
    public AKDModel() {
        setConfigPath(localVariablePath);
        variableList = new ArrayList<Variable>();
        variableMap = new HashMap<String, Variable>();
        inputObjMap = new HashMap<String, Object>();
        inputMap = new HashMap<String, String>();
        if (AKDModelVariables == null) {
            synchronized (AKDModel.class) {
                if (AKDModelVariables == null) {
                    try {
                        evaluator = pmmlFileLoad(ConfigurationManager.getConfiguration().getString("akd_model_pmml.pmml"));
                        AKDModelVariables = VariableContentHandler.readXML(localVariablePath);
                        register(this);
                    } catch (Exception e) {
                        logger.error("parse model config error", e);
                    }
                }
            }
        }
        for (Variable v : AKDModelVariables.getVariables()) {
            try {
                Variable requiredVariableClass = (Variable) v.clone();
                variableList.add(requiredVariableClass);
                variableMap.put(v.getName(), requiredVariableClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * load pmml file and generate evaluator
     */
//    private static void pmmlFileLoad() {
//        try {
//            String localPmmlPath = ConfigurationManager.getConfiguration().getString("akd_model_pmml.pmml", "maas-model/src/main/resources/model_config/akd_model_pmml.pmml");
//
//            pmml = PMMLUtils.loadPMML(localPmmlPath);
//            Model m = pmml.getModels().get(0);
//            evaluator = ModelEvaluatorFactory.getInstance().getModelManager(pmml, m);
//        } catch (Exception e) {
//            logger.error("load pmml file error !", e);
//        }
//    }


    /**
     * 重写了父类的work方法,定时任务调用父类的loadAllModelRes()方法的时,
     * 会依次执行在modelInstances中注册的实体的work方法,加载与该模型相关的方法,
     * modelInstances的模型不会重复.
     */
    @Override
    public void work() {

    }

    public double scoreToLogit(double prob) {
        return Math.log(1 / prob - 1);
    }

    public double scoreAlign(double logit) {
        return alignOffset + alignFactor * logit;
    }

    /**
     * execute model
     *
     * @return model score as a integer (1-1000)
     */
    public Object executeModel() {

        ArrayList<Double> scores = getScores(variableMap,resultFieldName,evaluator);

        double prob = scores.get(0);

        return Math.min(Math.max(Math.round(scoreAlign(scoreToLogit(prob))), 350), 900);
    }

    public String toString() {
        return "AiKaDaiModel";
    }

    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
