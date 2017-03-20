package com.ecreditpal.maas.model.variables;


import com.ecreditpal.maas.common.WorkDispatcher;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 年龄计算,截止到某个日期
 * 变量需求
 * birthday:生日时间
 * nowaday:最近的时间
 *
 * @author lifeng
 * @version 1.0 on 2017/3/1.
 */
public class AgeVariable extends Variable {
    private final static Logger logger = LoggerFactory.getLogger(AgeVariable.class);
    private static DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
    private static DateTimeFormatter dayTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();

    @Override
    public void execute(final CountDownLatch cdl, final Map<String, Object> map) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Object a = map.get("birthday");
                Object b = map.get("loanDay");
                if (a == null || b == null) {
                    setValue(MISSING);
                    return;
                }
                try {
                    String aStr = a.toString().trim();
                    String bStr = b.toString().trim();
                    if (StringUtils.isEmpty(aStr) || StringUtils.isEmpty(bStr)) {
                        setValue(MISSING);
                    }
                    DateTime birthday = getDateTime(aStr);
                    DateTime nowaday = getDateTime(bStr);
                    int age = Years.yearsBetween(birthday, nowaday).getYears();
                    setValue(String.valueOf(age));
                } catch (Exception e) {
                    logger.error("variable calculate error", e);
                    setValue(NUMERICAL_INVALID);

                } finally {
                    cdl.countDown();
                }
            }
        };
        WorkDispatcher.getInstance().modelExecute(t);
    }



    private DateTime getDateTime(String s) {
        DateTime dateTime;
        if (s.length() < 12) {
            dateTime = dayFormatter.parseDateTime(s);
        } else {
            dateTime = dayTimeFormatter.parseDateTime(s);
        }
        return dateTime;
    }

}
