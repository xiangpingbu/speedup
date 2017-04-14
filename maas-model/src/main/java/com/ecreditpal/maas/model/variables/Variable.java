package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.utils.OkHttpUtil;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
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
    private String type;
    /**
     * 变量计算完毕后的返回值,默认为9999
     */
    private String value = "-9999";
    /**
     * 变量计算依赖的值,在xml中需要预先配置
     */
    private VariableParam param;
    /**
     * 表示variable是由某个通用的类来计算的
     */
    private String engine;


    @XmlAttribute(name = "Name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "Description", required = true)
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

    @XmlElement(name = "type", required = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void execute(Map<String, String> inputMap, CountDownLatch cdl) {

    }

    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    @XmlElement(name = "param",required = false)
    public VariableParam getParam() {
        return param;
    }

    public void setParam(VariableParam param) {
        this.param = param;
    }
}
