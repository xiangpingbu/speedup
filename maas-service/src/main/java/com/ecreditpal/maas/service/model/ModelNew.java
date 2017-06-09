package com.ecreditpal.maas.service.model;


import avro.shaded.com.google.common.collect.Lists;
import com.ecreditpal.maas.common.WorkDispatcher;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.common.avro.LookupEventMessage.VariableResult;
import com.ecreditpal.maas.common.constants.CommonTypeEnum;
import com.ecreditpal.maas.model.reload.Register;
import com.ecreditpal.maas.model.reload.ResReload;

import com.ecreditpal.maas.service.model.variables.Variable;
import com.ecreditpal.maas.service.model.variables.VariableConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.dmg.pmml.FieldName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xibu on 9/28/16.
 */

@Getter
@Setter
public class ModelNew extends PmmlModel implements Register{
    private final static Logger log = LoggerFactory.getLogger(ModelNew.class);

    public String configPath;
    public String modelName;
    public List<Variable> variableList;
    public Map<String, Variable> variableMap;
    public Map<String, String> inputMap;
    public Map<String, Object> inputObjMap;
    public String inputJsonString;
    public String packagePath = "com.ecreditpal.maas.service.model.variables.";
    public List<HashMap<FieldName, String>> variablePool;
    public CountDownLatch cdl;
    private int CDL_TIMEOUT_SEC = 5;

    public static WorkDispatcher workDispatcher = new WorkDispatcher.Builder().
            corePoolSize(16).
            maxPoolSize(30).
            keepAliveTime(60).
            queue(new LinkedBlockingQueue<>(50)).build();

    //Runtime.getRuntime().availableProcessors()

    //loaded model will be put into this map;
    public static Map<String, ModelNew> modelInstances = new ConcurrentHashMap<>();




    public void register(ModelNew modelNew) {
        ResReload.getRegister().put(modelNew.toString(), modelNew);
    }


    //this method will be invoked by subclass
    public void work() {
    }

    //input is always JSONString passed from EndPoint
//    public Object run(String input) throws JAXBException {
//        setInputJsonString(input);
//        inputParse();
//        try {
//            invokeVariable();
//        } catch (Exception ignored) {
//        }
//        return executeModel();
//    }

    /**
     * 如果入参包含复杂对象,可以使用该方法传入参数
     * @param map 外部构造的map对象
     */
    public Object run(Map<String,?> map) {
        for (String s : map.keySet()) {
            Object val = map.get(s);
            if (CommonTypeEnum.isCommonType(val)) {
                inputMap.put(s,val.toString());
            }else {
                inputObjMap.put(s,val);
            }
        }
        try {
            invokeVariable();
        } catch (Exception ignored) {
        }
        return executeModel();
    }

//    private void inputParse() {
//        //overwrite in specific model class if special input treatment required
//        inputJsonParse(getInputJsonString());
//    }
//
//    private void inputJsonParse(String inputJsonString) {
//        JSONObject json = new JSONObject(inputJsonString);
//        Iterator<String> keys = json.keys();
//        HashMap<FieldName, String> inputVarList = new HashMap<FieldName, String>();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String val = json.get(key).toString();
//
//            if (val.equals("null")) {
//                inputMap.put(key, null);
//            } else {
//                inputMap.put(key, val);
//            }
//        }
//    }


//    private void inputMapParse(Map<String,?> map) {
//        for (String s : map.keySet()) {
//            inputObjMap.put(s,map.get(s));
//        }
//    }

    private void invokeVariable() {
        //overwrite in specific model class if special invoke order required
        //multi-thread with CountDownLatch
        cdl = new CountDownLatch(variableList.size());
            for (Variable v : variableList) {
                v.setInputMap(inputMap);
                v.setInputObjMap(inputObjMap);
                v.execute(cdl);
            }
        try {
            cdl.await(CDL_TIMEOUT_SEC, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Model base class Should not be called
    public Object executeModel() {
        return -999.0;
    }

    public List<Variable> loadVariableConfig() throws JAXBException {
        File xml = new File(configPath);
        if (!xml.exists()) {
            throw new RuntimeException("Cannot find variable configuration file " + configPath + " in the classpath");
        }
        JAXBContext jc = JAXBContext.newInstance(VariableConfiguration.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        VariableConfiguration conf = (VariableConfiguration) unmarshaller.unmarshal(xml);
        modelName = conf.getModel();

        return conf.getVariables();
    }



    /**
     * 将模型计算过程中变量产生的结果,和总的结果加入
     * @param variables 包含计算结果的变量
     * @param result 模型输出的结果
     * @return ModelLog
     */
    public ModelLog ParseVariables( List<Variable> variables,String result,String name) {
        List<VariableResult> variableResults = Lists.newArrayListWithCapacity(variables.size());
        variables.forEach(variable -> variableResults.add(new VariableResult(variable.getName(),variable.getValue().toString())));

        ModelLog modelLog = new ModelLog();
        modelLog.setModelResult(result);
        modelLog.setVariableResult(variableResults);
        modelLog.setModelName(name);
       return modelLog;
    }




}
