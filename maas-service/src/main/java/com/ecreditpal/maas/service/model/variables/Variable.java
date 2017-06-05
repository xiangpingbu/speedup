package com.ecreditpal.maas.service.model.variables;

import com.ecreditpal.maas.common.utils.http.OkHttpUtil;
import com.ecreditpal.maas.service.model.handler.RequestHandler;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xibu on 9/28/16.
 */

@XmlRootElement(name = "Variable")
@Getter
@Setter
public class Variable implements Cloneable {
    //protected static String domain = ConfigurationManager.getConfiguration().getString("api.domain");
//    protected static String domain = "http://panda.mycreditpal.com:8888";

    protected static OkHttpUtil httpClient = OkHttpUtil.getInstance();

    static final String NUMERICAL = "numerical";
    static final String CATEGORICAL = "categorical";
    /**如果无法得到变量值,默认为missing*/
    static final String MISSING = "missing";
    /**如果数值型变量的值非法,默认设置为"-999998"*/
    static final String NUMERICAL_INVALID = "-999999";
    /**如果分类数据的值非法,默认设置为"invalid"*/
    static final String CATEGORICAL_INVALID = "invalid";
    /**variable参数对应的关键字*/

    /**值非法,默认也为"invalid"*/
    private String invalid = CATEGORICAL_INVALID;

    private String key;
    /**变量的名称*/
    private String name;
    /**变量的描述*/
    private String description;
    /** 变量计算完毕后返回值的类型*/
    private String returnType;
    /**额外参数的类型*/
    private String paramType;
    /**额外参数的值*/
    private String paramValue;
    /**额外参数的其他映射*/
    private String paramMapping;
    /**变量计算完毕后的返回值,默认为9999*/
    private Object value=9999;
    /**表示variable是由某个通用的类来计算*/
    private String engine;

    private RequestHandler requestHandler;

    private Map<String,String> inputMap;

    private Map<String,Object> inputObjMap;


    public void execute(CountDownLatch cdl) {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String  getString(String key) {
        return inputMap.get(key);
    }

    public <T> T getObj(String key,Class<T> clz) {
        Object obj = inputObjMap.get(key);
        if (obj != null) {
            return  clz.cast(obj);
        } else{
            return null;
        }
    }

    /**
     * 根据变量的类型获得非法值
     * @return
     */
    public String getInvalid(){
        return NUMERICAL.equals(getParamType()) ? NUMERICAL_INVALID : CATEGORICAL_INVALID;
    }

    public boolean containsKey(String key) {
        return inputMap.containsKey(key) || inputObjMap.containsKey(key);
    }
}