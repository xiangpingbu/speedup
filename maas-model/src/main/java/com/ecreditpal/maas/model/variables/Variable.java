package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.utils.OkHttpUtil;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class Variable implements Cloneable {
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


    public void execute(Map<String, String> inputMap, CountDownLatch cdl) {
    }

    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();

    }
}