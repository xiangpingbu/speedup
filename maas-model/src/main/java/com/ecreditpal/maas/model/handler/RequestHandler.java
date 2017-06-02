package com.ecreditpal.maas.model.handler;

import java.util.Map;

/**
 * @author lifeng
 * @version 2017/4/14.
 */
public abstract class RequestHandler {
    public  <T> T execute(RequestHandler handler, RequestParam param,Class<T> clz){
        return null;
    };

    /**
     * 为链式调用做准备
     * @param handler 内部的处理者
     * @param param 请求参数
     * @return String
     */
    public  String execute(RequestHandler handler,Map<String,Object> param){
        return null;
    };

    public  String execute(Map<String,Object> param){
        return null;
    };

}
