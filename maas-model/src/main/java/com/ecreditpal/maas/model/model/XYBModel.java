package com.ecreditpal.maas.model.model;


import com.ecreditpal.maas.common.file.FileUtil;
import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.model.variables.Variable;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class XYBModel extends ModelNew {
    private final static Logger logger = LoggerFactory.getLogger(XYBModel.class);
    public static String localVariablePath = FileUtil.getFilePath("model_config/xyb_model_variables.xml");
    public static String localPmmlPath = FileUtil.getFilePath("model_config/xyb_model_pmml.pmml");
    private static List<Variable> XYBModelVariables;
    private static String resultFieldName = "RawResult";
    private static Double alignOffset = 483.9035953;
    private static Double alignFactor = 72.13475204;
    private Double prob;//未处理的总分数


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
     * @throws JAXBException
     */
    public XYBModel() throws JAXBException {
        setConfigPath(localVariablePath);
        variableList = new ArrayList<Variable>();
        variableMap = new HashMap<String, Variable>();
        inputObjMap = new HashMap<String, Object>();
        if (XYBModelVariables == null) {
            synchronized (XYBModel.class) {
                if (XYBModelVariables == null) {
                    XYBModelVariables = loadVariableConfig();
                }
            }
        }
        for (Variable v : XYBModelVariables) {
            try {
                String className;
                if (v.getEngine() != null) {
                    className = packagePath + v.getEngine();
                } else {
                    className = packagePath + v.getName();
                }
                Class c = Class.forName(className);
                Variable requiredVariableClass = (Variable) c.newInstance();
                requiredVariableClass.setName(v.getName());
                requiredVariableClass.setDescription(v.getDescription());
                requiredVariableClass.setParam(v.getParam());
                requiredVariableClass.setType(v.getType());
                variableList.add(requiredVariableClass);
                variableMap.put(v.getName(), requiredVariableClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        register(this);
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
        setProb(prob);
        int finalScore = (int)scoreAlign(scoreToLogit(prob));

        return finalScore;
    }

    public String toString() {
        return "XinYongBao";
    }


    public static void main(String[] args) throws JAXBException {
        XYBModel xybModel = new XYBModel();
    }

    public Double getProb() {
        return prob;
    }

    public void setProb(Double prob) {
        this.prob = prob;
    }
}
