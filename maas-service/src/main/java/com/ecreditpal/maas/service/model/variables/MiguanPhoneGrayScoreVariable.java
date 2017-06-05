package com.ecreditpal.maas.service.model.variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecreditpal.maas.common.WorkDispatcher;
import com.ecreditpal.maas.model.bean.model.miguan.MiguanVariableBean;
import com.ecreditpal.maas.service.model.handler.RequestHandler;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
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
    public void execute(CountDownLatch cdl) {
        Thread t = new Thread() {
            @Override
            public void run() {
                String invalid = getInvalid();
                try {
                    if (getString("phoneGrayScore") != null) {
                        setValue(getString("phoneGrayScore"));
                        return;
                    }

                    if (!containsKey("miguan_cid") &&
                            !containsKey("miguan_name") &&
                            !containsKey("miguan_mobile")) {
                        setValue(invalid);
                        return;
                    }

                    HashMap<String, Object> map = new HashMap<>(8);
                    Object value;
                    if ((value = getString("miguan_cid")) != null) {
                        map.put("cid", value);
                    }
                    if ((value = getString("miguan_name")) != null) {
                        map.put("name", value);
                    }
                    if ((value = getString("miguan_mobile")) != null) {
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
                    setValue(MISSING);
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
