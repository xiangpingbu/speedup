package com.ecreditpal.maas.model.variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecreditpal.maas.common.WorkDispatcher;
import com.ecreditpal.maas.model.bean.model.miguan.MiguanVariableBean;
import com.ecreditpal.maas.model.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/2.
 */
@Slf4j
public class MiguanPhoneGrayScoreVariable extends Variable {
    private String apiCode = "P310";

    @Override
    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        Thread t = new Thread() {
            @Override
            public void run() {
                String invalid = NUMERICAL.equals(getParamType()) ? NUMERICAL_INVALID : INVALID;
                try {

                    if (!inputMap.containsKey("cid") &&
                            !inputMap.containsKey("name") &&
                            !inputMap.containsKey("mobile")) {
                        setValue(invalid);
                        return;
                    }

                    HashMap<String, Object> map = new HashMap<>();
                    Object value;
                    if ((value = inputMap.get("cid")) != null) {
                        map.put("cid", value);
                    }
                    if ((value = inputMap.get("name")) != null) {
                        map.put("name", value);
                    }
                    if ((value = inputMap.get("mobile")) != null) {
                        map.put("mobile", value);
                    }
                    if (map.size() < 3) {
                        setValue(MISSING);
                        return;
                    }

                    map.put("apiCode", apiCode);

                    RequestHandler requestHandler = getRequestHandler();
                    String jsonResult = requestHandler.execute(map);

                    if (StringUtils.isEmpty(jsonResult)) {
                        setValue(MISSING);
                        return;
                    }
                    MiguanVariableBean bean = parseMiguanBean(jsonResult);
                    setValue(bean.getGrayProfile().getPhoneGrayScore());
                } catch (Exception e) {
                    log.error("error while parse Miguan api", e);
                    setValue(invalid);
                } finally {
                    cdl.countDown();
                }
            }
        };
        WorkDispatcher.getInstance().modelExecute(t);
    }

    public MiguanVariableBean parseMiguanBean(String json) {
        JSONObject jsonObject = JSON.parseObject(json);

        MiguanVariableBean bean = jsonObject.getJSONObject("data").
                getJSONObject("MOBILE_REPORT").
                getObject("result", MiguanVariableBean.class);
        return bean;
    }
}
