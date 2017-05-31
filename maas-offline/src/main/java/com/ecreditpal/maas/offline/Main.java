package com.ecreditpal.maas.offline;

import com.ecreditpal.maas.common.utils.CommonUtils;
import com.ecreditpal.maas.common.utils.json.JsonUtil;
import com.ecreditpal.maas.model.bean.Result;
import com.ecreditpal.maas.offline.audit.apply.ToolApply;
import org.apache.avro.util.internal.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/26.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("请指定target");
            return;
        }
        String target = args[0];
        Map<String, String> map = new HashMap<>();

        /*
         * 处理请求的参数
         */
        if (args.length > 1) {
            String paramStr = args[1];
            String[] array = paramStr.split("&");
            for (String s : array) {
                Integer equalSignIndex = s.indexOf("=");
                String key = s.substring(0, equalSignIndex);
                String value = s.substring(equalSignIndex + 1, s.length());
                map.put(key, value);
            }
        }

        switch (target) {
            case "apply":
                apply(map);
        }
    }

//    public static void main(String[] args) {
//        for (String arg : args) {
//            System.out.println(arg);
//        }
//    }

    public static void apply(Map<String, String> map) {
        //原始文件经过apply后的文件
        String applied = map.get("applied");
        //原始文件
        String origin = map.get("origin");
        String columnConfig = map.get("columnConfig");
        try {
            ToolApply.compare(applied, origin, columnConfig);
            Result<Boolean> result = Result.wrapSuccessfulResult(MessageBuilder.build(),Boolean.TRUE);
            System.out.println(JsonUtil.toJson(result));
            return;
        } catch (Exception e) {
            MessageBuilder.println("文件解析异常");
            MessageBuilder.println(e.getMessage());
        }
        Result<Boolean> result = Result.wrapErrorResult("-1",MessageBuilder.build());
        System.out.println(JsonUtil.toJson(result));
    }


}
