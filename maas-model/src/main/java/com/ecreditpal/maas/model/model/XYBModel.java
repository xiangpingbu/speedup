package com.ecreditpal.maas.model.model;


import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.utils.file.FileUtil;
import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.model.variables.Variable;
import com.ecreditpal.maas.model.variables.VariableContentHandler;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.JAXBException;
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
    public static String localVariablePath = ConfigurationManager.getConfiguration().getString("test_variables.xml");
    public static String localPmmlPath = ConfigurationManager.getConfiguration().getString("xyb_model_pmml.pmml");
    private static List<Variable> XYBModelVariables;
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
                    try {
                        XYBModelVariables = VariableContentHandler.readXML(localVariablePath).getVariables();
                    } catch (Exception e) {
                        logger.error("parse model config error",e);
                    }
                }
            }
        }
        for (Variable v : XYBModelVariables) {
            try {
                Class c =  Class.forName(v.getClassName());
                Variable requiredVariableClass = (Variable) c.newInstance();
                BeanUtils.copyProperties(v, c.newInstance());
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
        int finalScore = (int)scoreAlign(scoreToLogit(prob));

        return finalScore;
    }

    public String toString() {
        return "XinYongBao";
    }


    public static void main(String[] args) throws JAXBException {
        XYBModel xybModel = new XYBModel();
    }
}
