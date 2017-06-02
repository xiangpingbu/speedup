package com.ecreditpal.maas.model.handler;

import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.utils.http.ApacheHttpClient;
import com.ecreditpal.maas.common.utils.http.MyHttpClient;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/14.
 */
@Slf4j
public class HttpHandler extends RequestHandler {
    private static MyHttpClient myHttpClient = MyHttpClient.getInstance();
    private static String ecreditpalHost = "https://" +
            ConfigurationManager.getConfiguration().getString("maas.headstream");

    @Override
    public String execute(RequestHandler handler, Map<String, Object> param) {
        String host;
        if (param.get("url") == null)
            host = ecreditpalHost;
        else
            host = param.get("url").toString();
        try {
            return myHttpClient.post(host, param);
        } catch (Exception e) {
            log.error("error sending http request", e);
        }
        return null;
    }

    @Override
    public String execute(Map<String, Object> param) {
        return execute(null, param);
    }

}
