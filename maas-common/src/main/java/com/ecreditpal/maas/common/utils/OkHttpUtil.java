package com.ecreditpal.maas.common.utils;

import okhttp3.*;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/27.
 */
@Deprecated
public class OkHttpUtil {
    private final okhttp3.OkHttpClient okHttpClient = getOkHttpClient();

    private final static OkHttpUtil okHttpUtil = new OkHttpUtil();


    public static OkHttpUtil getInstance() {
        return okHttpUtil;
    }

    private Call processGetParam(String url){
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        return okHttpClient.newCall(request);
    }

    /**
     * 同步执行get请求
     * @param url 请求地址
     * @return
     */
    public String doGet(String url) throws IOException {
        Call call = processGetParam(url);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 同步执行post请求
     * @param url 请求地址
     * @param map 参数,目前只支持form形式.
     * @param headers 请求头
     * @throws IOException
     */
    public String doPost(String url, Map<String, String> map, Map<String, String> headers) throws IOException {

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Request.Builder requestBuilder = new Request.Builder();

        if (map != null) {
            for (String s : map.keySet()) {
                formBodyBuilder = formBodyBuilder.add(s, map.get(s));
            }
        }

        if (headers != null) {
            for (String s : headers.keySet()) {
                requestBuilder = requestBuilder.addHeader(s, headers.get(s));
            }
        }

        RequestBody formBody = formBodyBuilder.build();
        Request request = requestBuilder
                .url(url)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);

        Response response = call.execute();
//        System.out.println(response.body().string() + " 调用时间:" + (System.currentTimeMillis() - b));
        return response.body().string();
    }


    /**
     * 异步执行get操作
     * @param url 请求地址
     */
    public void getAsynHttp(String url) {
        final long b = System.currentTimeMillis();
        Call mcall = processGetParam(url);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    System.out.println(str);
                } else {
                    System.out.println(response.body().string());
                    System.out.println(System.currentTimeMillis()-b);
                }
            }
        });

    }

    public void postAsynHttpForm(String url, Map<String, String> map, Map<String, String> headers) throws IOException {

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Request.Builder requestBuilder = new Request.Builder();

        if (map != null) {
            for (String s : map.keySet()) {
                formBodyBuilder = formBodyBuilder.add(s, map.get(s));
            }
        }

        if (headers != null) {
            for (String s : headers.keySet()) {
                requestBuilder = requestBuilder.addHeader(s, headers.get(s));
            }
        }

        final long b = System.currentTimeMillis();
        RequestBody formBody = formBodyBuilder.build();
        Request request = requestBuilder
                .url(url)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();

                System.out.println(str);
                System.out.println("调用时间:"+(System.currentTimeMillis()-b));
            }

        });


    }



    private static okhttp3.OkHttpClient getOkHttpClient() {
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10,5,TimeUnit.MINUTES))
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);
        return builder.build();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        //OkHttpUtil.getInstance().doGet("http://localhost:8888/rest/restService/getUserJson");
        //Thread.sleep(10);
//        ThreadTest a = new ThreadTest();
//        ThreadTest b = new ThreadTest();
//         b.start();
//          Thread.sleep(1000);
//        for (int i = 0; i < 25; i++) {
//            ThreadTest a = new ThreadTest();
//            a.start();
////        a = new ThreadTest();
////        a.start();
//        }`
////        Thread.sleep(500);
//        for (int i = 0; i < 25; i++) {
//            ThreadTest a = new ThreadTest();
//            a.start();
////        a = new ThreadTest();
////        a.start();
//        }

        while(true) {
            Thread.sleep(20);
            ThreadTest a = new ThreadTest();
            a.start();
        }
//        Thread.sleep(500);
//        for (int i = 0; i < 2; i++) {
//            ThreadTest a = new ThreadTest();
//            a.start();
////        a = new ThreadTest();
////        a.start();
//        }
//        for (int i = 0; i < 25; i++) {
//            ThreadTest2 b = new ThreadTest2();
//            b.start();
//        b = new ThreadTest2();
//        b.start();
//        }
    }

    public static class ThreadTest extends Thread {
        public void run() {
            long time = System.currentTimeMillis();
            try {
                System.out.println(OkHttpUtil.getInstance().doGet("http://localhost:8888/rest/restService/getUserJson?ip=127.0.0.2"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(" "+(System.currentTimeMillis() - time));
        }
    }

    public static class ThreadTest2 extends Thread {
        public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long time = System.currentTimeMillis();
                try {
                    System.out.println(OkHttpUtil.getInstance().doGet("http://localhost:8888/rest/restService/getUserJson?ip=127.0.0.1"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print(" " + (System.currentTimeMillis() - time));
        }
    }

}
