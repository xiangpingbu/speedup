package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.utils.OkHttpUtil;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xibu on 9/28/16.
 */

@XmlRootElement(name = "Variable")
public class Variable {
    //protected static String domain = ConfigurationManager.getConfiguration().getString("api.domain");
    protected static String domain = "http://panda.mycreditpal.com:8888";
    protected static OkHttpUtil httpClient = OkHttpUtil.getInstance();
    /**
     * 如果无法得到变量值,默认为missing
     */
    protected static final String MISSING = "missing";
    /**
     * 如果数值型变量的值非法,默认设置为"-999998"
     */
    protected static final String NUMERICAL_INVALID = "-999999";
    /**
     * 如果分类数据的值非法,默认设置为"invalid"
     */
    protected static final String CATEGORICAL_INVALID = "invalid";
    /**
     * 值非法,默认也为"invalid"
     */
    protected static final String INVALID = CATEGORICAL_INVALID;

    private String key;

    private String className;
    /**
     * * 变量的名称
     */
    private String name;
    /**
     * 变量的描述
     */
    private String description;
    /**
     * 变量计算完毕后返回值的类型
     */
    private String returnType;

    private String paramType;

    private String paramValue;

    private String paramMapping;
    /**
     * 变量计算完毕后的返回值,默认为9999
     */
    private String value = "-9999";

    /**
     * 表示variable是由某个通用的类来计算的
     */
    private String engine;


    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description", required = true)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    @XmlElement(name = "Engine")
    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }


    public void execute(Map<String, String> inputMap, CountDownLatch cdl) {
    }

    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamMapping(String paramMapping) {
        this.paramMapping = paramMapping;
    }

    public String getParamMapping() {
        return paramMapping;
    }
}
