package com.ecreditpal.maas.common.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
public class HTTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(HTTPUtil.class);

    public static void consumeResponse(HttpResponse response) {
        try {
            HttpEntity responseEntity = response.getEntity();
            EntityUtils.consume(responseEntity);

        }
        catch (Exception e) {
            logger.error("Failed to consume response: ", e);
        }
    }

}
