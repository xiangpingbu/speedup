package com.ecreditpal.maas.service.model.variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecreditpal.maas.common.WorkDispatcher;
import com.ecreditpal.maas.common.constants.MaasConstants;
import com.ecreditpal.maas.model.bean.IdCardInfoBean;
import com.ecreditpal.maas.service.IdcardAnalyzeService;
import com.ecreditpal.maas.service.MobileAnalyzeService;
import com.ecreditpal.maas.service.model.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/5.
 *          返回用户手机所在地和身份证是否匹配
 */
@Slf4j
public class CidPhoneLocationVariable extends Variable {

    @Override
    public void execute(CountDownLatch cdl) {

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    if (getString("pIdCitySameInd") != null) {
                        if ("TRUE".equals(getString("pIdCitySameInd"))){
                            setValue(MaasConstants.CID_PHONE_LOCATION_EQUALS);
                        }else{
                            setValue(MaasConstants.CID_PHONE_LOCATION_UN_EQUALS);
                        }
                        return;
                    }

                    String cid = getString("cid");
                    String mobile = getString("mobile");

                    if (cid == null && mobile == null) {
                        setValue(MISSING);
                        return;
                    }
                    IdcardAnalyzeService idcardAnalyzeService = IdcardAnalyzeService.getInstance();
                    MobileAnalyzeService mobileAnalyzeService = MobileAnalyzeService.getInstance();
                    if(!idcardAnalyzeService.check(cid) || !mobileAnalyzeService.isValidMobileNumber(mobile)){
                        setValue(getInvalid());
                        return;
                    }

                    IdCardInfoBean bean = idcardAnalyzeService.parseToBean(cid);
                    String region = bean.getRegion();

                    RequestHandler requestHandler = getRequestHandler();

                    Map<String, Object> map = new HashMap<>(2);
                    map.put("mobile", mobile);
                    String response = requestHandler.execute(map);

                    JSONObject obj = JSON.parseObject(response);
                    JSONObject result = obj.getJSONObject("data").getJSONObject("MOBILE_LOCATION");
                    if (result.getInteger("code") == 1) {
                        String province = result.getJSONObject("result").getString("province");
                        String city = result.getJSONObject("result").getString("city");
                        if (region.contains(province) && region.contains(city)) {
                            setValue(MaasConstants.CID_PHONE_LOCATION_EQUALS); //匹配
                        } else{
                            setValue(MaasConstants.CID_PHONE_LOCATION_UN_EQUALS); //未匹配
                        }
                    }
                }catch(Exception e) {
                    log.error("error while comparing cid location and phone location",e);
                    setValue(MISSING);
                } finally {
                    cdl.countDown();
                }
            }
        };

        WorkDispatcher.getInstance().modelExecute(t);

    }
}