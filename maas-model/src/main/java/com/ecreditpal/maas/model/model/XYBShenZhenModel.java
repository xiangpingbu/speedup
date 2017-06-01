package com.ecreditpal.maas.model.model;


import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.model.variables.Variable;
import com.ecreditpal.maas.model.variables.VariableConfiguration;
import com.ecreditpal.maas.model.variables.VariableContentHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
@Getter
@Setter
@Slf4j
public class XYBShenZhenModel extends ModelNew {
    public static String localVariablePath = ConfigurationManager.getConfiguration().getString("xyb_ShenZhen_model_variable.xml");
    public static  VariableConfiguration modelVariables;
    private static String resultFieldName = "RawResult";
    private static Double alignOffset = 483.9035953;
    private static Double alignFactor = 72.13475204;

    public static PMML pmml;
    public static Evaluator evaluator;



    /**
     * static block. load pmml file and init evaluator
     *
     *
     */
    static {
        String localPmmlPath = ConfigurationManager.getConfiguration().getString("xyb_ShenZhen_model_pmml.pmml");
        pmmlFileLoad(localPmmlPath,pmml,evaluator);
    }

    /**
     * init required data structures
     * init variables and put variable into variable list
     * and variable map
     */
    public XYBShenZhenModel(){
        setConfigPath(localVariablePath);
        variableList = new ArrayList<Variable>();
        variableMap = new HashMap<String, Variable>();
        inputObjMap = new HashMap<String, Object>();
        if (modelVariables == null) {
            synchronized (XYBShenZhenModel.class) {
                if (modelVariables == null) {
                    try {
                        modelVariables = VariableContentHandler.readXML(localVariablePath);
                    } catch (Exception e) {
                        log.error("parse model config error",e);
                    }
                }
            }
        }
        for (Variable v : modelVariables.getVariables()) {
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



    public double scoreToLogit(double prob){
        return Math.log(1/prob-1);
    }

    public double scoreAlign(double logit){
        return alignOffset+alignFactor*logit;
    }

    /**
     *  execute model
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
        int finalScore = (int)scoreAlign(scoreToLogit(prob));

        return finalScore;
    }

    public String toString() {
        return "XinYongBao";
    }

}
