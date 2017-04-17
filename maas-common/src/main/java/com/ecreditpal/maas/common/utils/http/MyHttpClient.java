package com.ecreditpal.maas.common.utils.http;

import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author lifeng
 * @version 1.0 on 2017/4/16.
 */
@Slf4j
public class MyHttpClient {
    //应该以后可以复用吧...
    private static PoolingHttpClientConnectionManager manager = null;
    private static CloseableHttpClient httpClient = null;

    private static MyHttpClient myHttpClient = new MyHttpClient();

    public static MyHttpClient getInstance() {
        return  myHttpClient;
    }

    public static synchronized CloseableHttpClient getHttpClient(){
        if (httpClient == null) {
            //注册访问协议相关的socket工厂
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();
            //HttpConnection工厂:配置写请求/解析相应处理器
            HttpConnectionFactory<HttpRoute,ManagedHttpClientConnection> connectionFactory =
                    new ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE,
                            DefaultHttpResponseParserFactory.INSTANCE);
            //DNS解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
            //创建池化链接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,connectionFactory,dnsResolver);
            //默认为Socket配置
            SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            manager.setDefaultSocketConfig(defaultSocketConfig);
            //设置连接池的最大连接数
            manager.setMaxTotal(64);
            //每个路由的默认最大链接,每个路由实际最大连接数默认为DefaultMaxPerRoute控制,
            //而MaxTotal是控制整个池子的最大数
            //设置过小无法支持大并发(ConnectionPoolTimeoutException: Timeout waiting for connection from pool)
            //路由是对maxTotal的细分
            manager.setDefaultMaxPerRoute(32);
            //在连接池获取连接时,链接不活跃多长时间后需要进行一次验证,默认为2s
            manager.setValidateAfterInactivity(5*1000);

            String target = ConfigurationManager.getConfiguration().getString("maas.headstream","dolphin.myecreditpal.com");
            int port = ConfigurationManager.getConfiguration().getInt("maas.headstream.port",8888);
            manager.setMaxPerRoute(new HttpRoute(new HttpHost(target,port)),64);

            //默认请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(2*1000) // 设置链接超时时间
                    .setSocketTimeout(5*1000)//设置等待数据超时时间
                    .setConnectionRequestTimeout(2000)
                    //设置从连接池获取连接的等待超时时间
                    .build();

            //创建HttpClient
            httpClient = HttpClients.custom()
                    .setConnectionManager(manager)
                    .setConnectionManagerShared(false) //非共享模式连接池
                    .evictIdleConnections(600, TimeUnit.SECONDS)//定期回收空闲链接
                    .evictExpiredConnections()//定期回收过期链接a
                    //链接存活时间,如果不设置,则根据长连接信息决定
                    .setConnectionTimeToLive(60,TimeUnit.SECONDS)
                    //设置默认请求配置
                    .setDefaultRequestConfig(defaultRequestConfig)
                    //链接重用策略,即是否能keepAlive
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                    //长连接配置,即获取长连接生产多长时间
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                    //设置重试次数,默认是3次:当前是禁用状态
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0,false))
                    .build();
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    try {
                        httpClient.close();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return httpClient;
    }

    public  String get(String url) {
        HttpResponse response = null;
        HttpGet get = new HttpGet(url);
        try {
            response = getHttpClient().execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                EntityUtils.consume(response.getEntity());
            } else {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            log.error("error sending get request",e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error("error getting the entity",e);
                }
            }
        }
        return null;
    }

    public String post(String url, Map<String,Object> params) {
        HttpResponse response = null;
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nameValuePair = Lists.newArrayListWithCapacity(params.size());
        //lamba 表达式构造http请求所需的参数
        params.forEach((k,v) -> nameValuePair.add(new BasicNameValuePair(k,v.toString())));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePair));
            response = getHttpClient().execute(post);
            if (response.getStatusLine().getStatusCode() !=HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            } else {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            log.error("error sending post request the request", e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error("error getting the entity",e);
                }
            }
        }
        return null;
    }

}
