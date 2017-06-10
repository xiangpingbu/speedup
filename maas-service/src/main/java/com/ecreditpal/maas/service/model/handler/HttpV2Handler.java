package com.ecreditpal.maas.service.model.handler;

import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.utils.http.MyHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/14.
 */
@Slf4j
public class HttpV2Handler extends RequestHandler {
    private static MyHttpClient myHttpClient = MyHttpClient.getInstance();
    private static String ecreditpalHost = "https://" +
            ConfigurationManager.getConfiguration().getString("maas.headstream");
    private static String account =
            ConfigurationManager.getConfiguration().getString("v2.account");
    private static String password =
            ConfigurationManager.getConfiguration().getString("v2.password");
    @Override
    public String execute(RequestHandler handler, Map<String, String> param) {
        param.put("account",account);
        param.put("password",password);
        return myHttpClient.post(ecreditpalHost, param);
    }

    @Override
    public String execute(Map<String, String> param) {
        return execute(null, param);
    }



}
