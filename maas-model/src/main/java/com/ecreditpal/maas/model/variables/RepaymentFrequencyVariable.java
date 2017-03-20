package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.WorkDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 还款频率
 * 需求参数
 * repaymentFrequencyList:贷款的还款频率列表
 *
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class RepaymentFrequencyVariable extends Variable {
    private final static Logger logger = LoggerFactory.getLogger(RepaymentFrequencyVariable.class);


    @SuppressWarnings("unchecked")
    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Object obj = inputMap.get("repaymentFrequency");
                    if (obj == null) {
                        setValue(MISSING);
                    } else if (obj instanceof String) {
                        setValue(obj.toString());
                    } else if (obj instanceof List) {
                        List<Double> repaymentFrequencyList = (List<Double>) obj;
                        Double sum = null;
                        int count = 0;
                        if (repaymentFrequencyList.size() > 0) {
                            sum = 0.0;
                            for (Double a : repaymentFrequencyList) {
                                if (a != null && a > 0) {
                                    sum += a;
                                    count++;
                                }
                            }
                        }
                        if (sum == null) {
                            setValue(MISSING);
                            return;
                        }
                        if (count != 0) {
                            setValue(String.valueOf(sum / count));
                        } else {
                            setValue(MISSING);
                        }
                    }
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
