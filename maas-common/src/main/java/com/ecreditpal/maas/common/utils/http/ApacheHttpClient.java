package com.ecreditpal.maas.common.utils.http;

import com.ecreditpal.maas.common.avro.LookupEventMessage.VariableResult;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
public class ApacheHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClient.class);

    private static volatile ApacheHttpClient instance = null;
    private static DefaultHttpClient httpClient;
    private static PoolingClientConnectionManager connManager;

    private static volatile boolean initialized = false;
    private static Object initLock = new Object();

    public static ApacheHttpClient getInstance() {
        if (initialized) {
            return instance;
        }
        synchronized (initLock) {
            if (!initialized) {
                instance = new ApacheHttpClient();
                initialized = true;
            }
        }
        return instance;
    }

    private ApacheHttpClient() {
        long keepAliveTimeout = 60000;
        int maxConnection = 30;

        connManager = new PoolingClientConnectionManager();
        connManager.setDefaultMaxPerRoute(maxConnection);
        connManager.setMaxTotal(maxConnection);

        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 0);
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);

        httpClient = new DefaultHttpClient(connManager, httpParams);
        ConnectionKeepAliveStrategy strategy = new ConnectionKeepAliveStrategy(keepAliveTimeout);

        httpClient.setKeepAliveStrategy(strategy);

        int idleTimeout = 30000;

        new IdleConnectionMonitorThread(connManager, idleTimeout);

    }

    public String sendGetRequest(String url) throws URISyntaxException {
        HttpGet get = new HttpGet();
        get.setURI(new URI(url));
        get.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        return sendGetRequest(get);
    }


    public boolean sendPutRequest(String url) throws URISyntaxException {
        HttpPut put = new HttpPut();
        put.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        put.setURI(new URI(url));
        return sendPutRequest(put);
    }

    public String sendPostRequest(String url, List<NameValuePair> nameValuePair) throws UnsupportedEncodingException, URISyntaxException {
        HttpPost post = new HttpPost();
        post.setURI(new URI(url));
        post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

        if (nameValuePair != null) {
            post.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }

        return sendPostRequest(post);
    }

    public String sendPostRequest(String url, Map<String,Object> params) throws UnsupportedEncodingException, URISyntaxException {
        List<NameValuePair> nameValuePair = Lists.newArrayListWithCapacity(params.size());
        //lamba 表达式构造http请求所需的参数
        params.forEach((k,v) -> nameValuePair.add(new BasicNameValuePair(k,v.toString())));
        return sendPostRequest(url,nameValuePair);
    }

    public String sendPostRequest(String url, String jsonBody) throws URISyntaxException, UnsupportedEncodingException {
        HttpPost post = new HttpPost();
        post.setURI(new URI(url));
        post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

        if (jsonBody != null) {
            StringEntity input = new StringEntity(jsonBody, "UTF-8");
            //input.setContentType("application/json");
            post.setEntity(input);
            post.setHeader(HTTP.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        }
        return sendPostRequest(post);
    }

    public String sendUrlEncodedPostRequest (String url) throws URISyntaxException, UnsupportedEncodingException {
        HttpPost post = new HttpPost();
        post.setURI(new URI(url));
        post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        return sendPostRequest(post);
    }

    private boolean sendPutRequest(HttpPut put) {
        try {
            HttpResponse response = httpClient.execute(put);

            logger.info("response : {} for putUrl {}", response, put.getURI());

            int code = response.getStatusLine().getStatusCode();
            logger.info(response.getEntity().getContent().toString());
            HTTPUtil.consumeResponse(response);

            if (code != 200 && code != 201) {
                logger.error("Error sending http request with status code {} for put url {}", code, put.getURI());
                return false;
            }

            return true;
        }
        catch (Exception e) {
            logger.error("Exception while sending http request: ", e);
        }
        finally {
            if (put != null) {
                put.releaseConnection();
            }
        }
        return false;
    }

    private String sendPostRequest(HttpPost post) {
        try {
            HttpResponse response = httpClient.execute(post);

            logger.info("response : {} for postUrl {}", response, post.getURI());

            int code = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(),"gbk");
            logger.info(responseBody);
            HTTPUtil.consumeResponse(response);

            if (code != 200 && code != 201) {
                logger.error("Error sending http request with status code {} for post url {}", code, post.getURI());
                return null;
            }

            return responseBody;
        }
        catch (Exception e) {
            logger.error("Exception while sending http request: ", e);
        }
        finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        return null;
    }

    private String sendGetRequest(HttpGet get) {
        logger.debug("getUrl {}", get.getURI());
        String ret = "";

        try {
            HttpResponse response = httpClient.execute(get);
            logger.debug("response : {} for putUrl {}", response, get.getURI());

            int code = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            ret = EntityUtils.toString(entity);

            HTTPUtil.consumeResponse(response);
        } catch (SocketTimeoutException e) {
            logger.error("timeout while sending http request: ", e);
        } catch (Exception e) {
            logger.error("Exception while sending http request: ", e);
        }
        finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        return ret;
    }

}
