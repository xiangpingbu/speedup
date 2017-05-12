package com.ecreditpal.maas.model.model;


import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.model.variables.Variable;
import com.ecreditpal.maas.model.variables.VariableConfiguration;
import com.ecreditpal.maas.model.variables.VariableContentHandler;
import lombok.Getter;
import lombok.Setter;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
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
    public static String localPmmlPath = ConfigurationManager.getConfiguration().getString("akd_model_pmml.pmml");
    public static VariableConfiguration AKDModelVariables;
    private static String resultFieldName = "RawResult";
    private static Double alignOffset = 483.9035953;
    private static Double alignFactor = 72.13475204;


    /**
     * static block. load pmml file and init evaluator
     *
     *
     */
    static {
        pmmlFileLoad();
    }

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
        if (AKDModelVariables == null) {
            synchronized (AKDModel.class) {
                if (AKDModelVariables == null) {
                    try {
                        AKDModelVariables = VariableContentHandler.readXML(localVariablePath);
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
        register(this);
    }

    /**
     * load pmml file and generate evaluator
     */
    public static void pmmlFileLoad() {
        try {
            setPmml(PMMLUtils.loadPMML(localPmmlPath));
            org.dmg.pmml.Model m = pmml.getModels().get(0);
            ModelEvaluator<?> evaluator = ModelEvaluatorFactory.getInstance().getModelManager(pmml, m);
            setEvaluator(evaluator);
        } catch (Exception e) {
            logger.error("load pmml file error !", e);
        }
    }


    /**
     * 重写了父类的work方法,定时任务调用父类的loadAllModelRes()方法的时,
     * 会依次执行在modelInstances中注册的实体的work方法,加载与该模型相关的方法,
     * modelInstances的模型不会重复.
     */
    @Override
    public void work() {
        pmmlFileLoad();
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

        List<Map<FieldName, FieldValue>> input = prepareModelInput(evaluator, variableMap);
        ArrayList<Double> scores = new ArrayList<>();

        for (Map<FieldName, FieldValue> maps : input) {
            Map<FieldName, Double> regressionTerm = (Map<FieldName, Double>) evaluator.evaluate(maps);
            scores.add(regressionTerm.get(new FieldName(resultFieldName)).doubleValue());
        }

        double prob = scores.get(0);
        int finalScore = (int) scoreAlign(scoreToLogit(prob));

        return finalScore;
    }

    public String toString() {
        return "AiKaDaiModel";
    }

}