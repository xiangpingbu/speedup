package com.ecreditpal.maas.service.model.scorecard;


import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.service.model.ModelNew;
import com.ecreditpal.maas.service.model.variables.Variable;
import com.ecreditpal.maas.service.model.variables.VariableConfiguration;
import com.ecreditpal.maas.service.model.variables.VariableContentHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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


    /**
     * init required data structures
     * init variables and put variable into variable list
     * and variable map
     */
    public XYBShenZhenModel(){
        if (pmml ==null && evaluator ==null) {
            String localPmmlPath = ConfigurationManager.getConfiguration().getString("xyb_ShenZhen_model_pmml.pmml");
            pmmlFileLoad(localPmmlPath);
        }

        setConfigPath(localVariablePath);
        variableList = new ArrayList<Variable>();
        variableMap = new HashMap<String, Variable>();
        inputObjMap = new HashMap<String, Object>();
        inputMap = new HashMap<String,String>();
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
        List<Double> scores = getScores(variableMap,resultFieldName);
        double prob = scores.get(0);

//        return (int)scoreAlign(scoreToLogit(prob));
        return prob;
    }

    public String toString() {
        return "XinYongBaoShenZhen";
    }

}
