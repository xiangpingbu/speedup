package com.ecreditpal.maas.model.handler;

import com.ecreditpal.maas.common.utils.http.ApacheHttpClient;
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
public class HttpHandler implements RequestHandler {
    private ApacheHttpClient apacheHttpClient = ApacheHttpClient.getInstance();

    @Override
    public <T> T execute(RequestHandler handler, RequestParam param,Class<T> c) {
        return c.cast(null);
    }

    @Override
    public String execute(RequestHandler handler, RequestParam param) {
        HttpParam p = (HttpParam) param;
        try {
            return apacheHttpClient.sendPostRequest(p.getUrl(),p.getParam());
        } catch(Exception e) {
            log.error("error sending http request",e);
        }
        return null;
    }

}
