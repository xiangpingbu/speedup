package com.ecreditpal.maas.model.variables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecreditpal.maas.model.handler.RequestHandler;
import com.ecreditpal.maas.model.handler.RequestParam;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/2.
 */
public class MiguanVariable extends Variable {

    @Override
    public void execute(CountDownLatch cdl, Map<String, Object> inputMap) {
        RequestHandler requestHandler = this.getRequestHandler();
        String jsonResult = requestHandler.execute(inputMap);
        JSONObject jsonObject = new JSONObject();
    }

    public static void main(String[] args) {
       JSONObject jsonObject =  JSON.parseObject(s);
        jsonObject.getJSONArray("");
        for (Object o : jsonObject.getJSONArray("")) {

        }
//        jsonObject.
    }

    static String s = "{\n" +
            "    \"code\": 100,\n" +
            "    \"desc\": \"请求成功\",\n" +
            "    \"data\": {\n" +
            "        \"MOBILE_REPORT\": {\n" +
            "            \"code\": \"1\",\n" +
            "            \"result\": {\n" +
            "                \"basic_profile\": {\n" +
            "                    \"phone_province\": [\"浙江\",\"宁波\"],\n" +
            "                    \"phone_operator\": \"中国移动\",\n" +
            "                    \"user_province\": \"浙江省\",\n" +
            "                    \"gender\": \"男\",\n" +
            "                    \"phone\": \"13738121017\",\n" +
            "                    \"idcard\": \"330225199307267718\",\n" +
            "                    \"name\": \"叶李峰\",\n" +
            "                    \"user_region\": \"象山县\",\n" +
            "                    \"user_city\": \"宁波市\",\n" +
            "                    \"id_valid\": true,\n" +
            "                    \"phone_city\": \"杭州\",\n" +
            "                    \"age\": 23\n" +
            "                },\n" +
            "                \"query_history\": [\n" +
            "                    {\n" +
            "                        \"query_time\": \"2017-03-06\",\n" +
            "                        \"query_org\": \"其他\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"query_time\": \"2016-05-20\",\n" +
            "                        \"query_org\": \"其他\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"query_time\": \"2016-05-16\",\n" +
            "                        \"query_org\": \"其他\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"update_time\": 1496371100058,\n" +
            "                \"idcard_suspicion\": {\n" +
            "                    \"related_orgs\": [\n" +
            "                        {\n" +
            "                            \"susp_org_type\": \"其他\",\n" +
            "                            \"susp_updt\": \"2017-03-06 15:45:29\"\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"related_names\": [],\n" +
            "                    \"related_phones\": []\n" +
            "                },\n" +
            "                \"user_blacklist\": {\n" +
            "                    \"idcard_in_blacklist_updt\": \"\",\n" +
            "                    \"blacklist_category\": [],\n" +
            "                    \"idcard_in_blacklist\": false,\n" +
            "                    \"phone_in_blacklist\": false,\n" +
            "                    \"phone_in_blacklist_updt\": \"\"\n" +
            "                },\n" +
            "                \"gray_profile\": {\n" +
            "                    \"contacts_blacklist_class2_count\": 0,\n" +
            "                    \"contacts_blacklist_rate_v0\": 0,\n" +
            "                    \"contacts_blacklist_class1_count\": 0,\n" +
            "                    \"contacts_class1_count\": 3,\n" +
            "                    \"contacts_router_count\": 0,\n" +
            "                    \"phone_gray_score\": 77\n" +
            "                },\n" +
            "                \"register_history\": [],\n" +
            "                \"phone_suspicion\": {\n" +
            "                    \"related_idcards\": [],\n" +
            "                    \"related_orgs\": [\n" +
            "                        {\n" +
            "                            \"susp_org_type\": \"其他\",\n" +
            "                            \"susp_updt\": \"2017-03-06 15:45:29\"\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"related_names\": []\n" +
            "                }\n" +
            "            }\n" +
            "        },\n" +
            "        \"MAAS.M111\": {\n" +
            "            \"code\": 1,\n" +
            "            \"desc\": \"找到结果\",\n" +
            "            \"result\": {\n" +
            "                \"data\": \"514\",\n" +
            "                \"success\": true\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"mobile\": \"13738121017\",\n" +
            "        \"name\": \"叶李峰\",\n" +
            "        \"apiCode\": \"P310,M111\",\n" +
            "        \"cid\": \"330225199307267718\"\n" +
            "    }\n" +
            "}";
}
