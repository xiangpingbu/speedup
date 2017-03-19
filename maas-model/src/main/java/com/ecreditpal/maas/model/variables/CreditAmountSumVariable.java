package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.WorkDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * 信用卡额度求和
 * 变量需求
 * creditLimitList 信用卡额度列表
 *
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class CreditAmountSumVariable extends Variable {
    private final static Logger logger = LoggerFactory.getLogger(CreditAmountSumVariable.class);

    @SuppressWarnings("unchecked")
    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Object obj = inputMap.get("creditLimitList");
                    if (obj == null) {
                        setValue(MISSING);
                    } else if (obj instanceof String) {
                        setValue(obj.toString());
                    } else if(obj instanceof List) {
                        List<Double> amountList = (List<Double>) inputMap.get("creditLimitList");
                        Double sum = null;
                        int m = 0;
                        if (amountList != null && amountList.size() > 0) {
                            sum = 0.0;
                            for (Double a : amountList) {
                                if (a != null && a > 0) {
                                    sum += a;
                                    m++;
                                }
                            }
                            if (m == 0) {
                                setValue(MISSING);
                                return;
                            }
                        }
                        if (sum != null) {
                            setValue(sum.toString());
                        } else {
                            setValue(MISSING);
                        }
                    }
                    logger.info("calculator complete");
                } catch (Exception e) {
                    logger.error("variable calculate error", e);
                    setValue(INVALID);
                } finally {
                    cdl.countDown();
                }

            }

        };
        WorkDispatcher.getInstance().modelExecute(t);
    }
}
