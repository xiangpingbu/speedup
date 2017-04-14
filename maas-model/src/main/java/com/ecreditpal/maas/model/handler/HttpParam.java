package com.ecreditpal.maas.model.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lifeng
 * @version  2017/4/14.
 */
@Getter
@Setter
public class HttpParam implements RequestParam {

    private String url;
    private Map<String, Object> param;

    public HttpParam(String url,Map<String,Object> param) {
        this.url = url;
        this.param = param;
    }
}
