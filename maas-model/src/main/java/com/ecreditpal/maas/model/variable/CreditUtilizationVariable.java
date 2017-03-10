package com.ecreditpal.maas.model.variable;

import com.ecreditpal.maas.common.WorkDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 计算信用卡使用率
 * 变量需求
 * totalUsedLimit:已经使用的信用额度
 * totalCreditLimit:总的信用额度
 *
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class CreditUtilizationVariable extends Variable {
    private final static Logger logger = LoggerFactory.getLogger(CreditUtilizationVariable.class);
    private static BigDecimal zero = new BigDecimal(0);

    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Object a = inputMap.get("totalUsedLimit");
                Object b = inputMap.get("totalCreditLimit");
                if (a == null || b == null) {
                    setValue(MISSING);
                    return;
                }
                try {
                    //已经使用的信用额度
                    BigDecimal totalUsedLimit = new BigDecimal(a.toString());
                    //总的信用额度
                    BigDecimal totalCreditLimit = new BigDecimal(b.toString());
                    if (totalUsedLimit.signum() < 0 || totalCreditLimit.signum() < 0) {
                        setValue(INVALID);
                    }
                     else if ( zero.equals(totalCreditLimit)) {
                        setValue(MISSING);
                    }
                        else{
                            setValue(totalUsedLimit.multiply(new BigDecimal(100)).divide(totalCreditLimit, 1, BigDecimal.ROUND_HALF_UP).toString());
                            logger.info("calculator complete");
                        }
                    } catch(NumberFormatException e){
                        logger.error("数值转换异常", e);
                        setValue(INVALID);
                    } finally{
                        cdl.countDown();
                    }

                }
            }

            ;

        WorkDispatcher.getInstance().

            modelExecute(t);

        }

    }
