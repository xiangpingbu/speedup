package com.ecreditpal.maas.model.handler;

import java.util.Map;

/**
 * @author lifeng
 * @version 2017/4/14.
 */
public interface RequestHandler {
    public <T> T execute(RequestHandler handler, RequestParam param,Class<T> clz);

    public String execute(RequestHandler handler, RequestParam param);

}
