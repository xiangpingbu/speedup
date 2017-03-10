package com.ecreditpal.maas.model.variable;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class SectionVariable extends Variable {
    private final static Logger logger = LoggerFactory.getLogger(SectionVariable.class);


    @Override
    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        try {
            VariableParam param = getParam();
            String name = param.getParamName();
            Object val = inputMap.get(name);
            if (val == null) {
                setValue(MISSING);
                return;
            }
            String valStr = val.toString();
            if (valStr.length() == 0) {
                setValue(MISSING);
                return;
            }
            String setStr = param.getParamValue();
            //type为categorical的区间是独立的字符
            if ("categorical".equals(param.getParamType())) {
                try {
                    String[] rangeArray = setStr.split("\\|");
                    String[] mappings = null;
                    if (param.getMapping() != null) {
                        mappings = param.getMapping().split("\\|");
                    }
//                    Set<String> set = Sets.newHashSetWithExpectedSize(rangeArray.length);
                    Map<String, String> map = Maps.newHashMap();
                    if (mappings == null) {
                        for (String s : rangeArray) {
                            map.put(s, "");
                        }
                    } else {
                        for (int i = 0; i < rangeArray.length; i++) {
                            map.put(rangeArray[i], mappings[i]);
                        }
                    }
                    String result = map.get(valStr);
                    if (result == null) {
                        setValue(MISSING);
                        return;
                    }
                    setValue(result);
                } catch (Exception e) {
                    logger.error("exception occurs while processing the val", e);
                    setValue(CATEGORICAL_INVALID);
                }
            } else if ("numerical".equals(param.getParamType())) {
                try {
                    double dbVal = Double.valueOf(valStr);
                    String[] array = setStr.split("-");
                    String leftBra = array[0].substring(0, 1);
                    String leftLimit = array[0].substring(1, array[0].length());

                    String rightBra = array[1].substring(array[1].length() - 1, array[1].length());
                    String rightLimit = array[1].substring(0, array[1].length() - 1);
                    //负无穷到特定值
                    if ("min".equals(leftLimit) && !"max".equals(rightLimit)) {
                        hitRight(rightLimit, rightBra, dbVal);
                        //特定值到正无穷
                    } else if (!"min".equals(leftLimit) && "max".equals(rightLimit)) {
                        hitLeft(leftLimit, leftBra, dbVal);
                    }
                    //最小值和最大值都有限制
                    else {
                        hitRight(rightLimit, rightBra, dbVal);
                        hitLeft(leftLimit, leftBra, dbVal);
                    }
                } catch (Exception e) {
                    logger.error("exception occurs while processing the val", e);
                    setValue(NUMERICAL_INVALID);
                }
                setValue(valStr);
            }
        } finally {
            cdl.countDown();
        }
    }

    /**
     * 如果超出左边的边界,设置变量非法
     *
     * @param leftLimit 边界值
     * @param leftBra   括号开闭情况
     * @param val       变量值
     */
    private void hitLeft(String leftLimit, String leftBra, double val) {
        if (isClosed(leftBra)) {
            if (val < Double.valueOf(leftLimit)) {
                setValue(NUMERICAL_INVALID);
            }
        } else {
            if (val <= Double.valueOf(leftLimit)) {
                setValue(NUMERICAL_INVALID);
            }
        }
    }

    /**
     * 如果超过右边边界,设置变量非法
     *
     * @param rightLimit 边界值
     * @param rightBra
     * @param val
     */
    private void hitRight(String rightLimit, String rightBra, double val) {
        if (isClosed(rightBra)) {
            if (val > Double.valueOf(rightLimit)) {
                setValue(NUMERICAL_INVALID);
            }
        } else {
            if (val >= Double.valueOf(rightLimit)) {
                setValue(NUMERICAL_INVALID);
            }
        }
    }

    private boolean isClosed(String c) {
        return "[".equals(c) || "]".equals(c);
    }

}
