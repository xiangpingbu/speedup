package com.ecreditpal.maas.web.endpoint.interceptor;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lifeng
 * @CreateTime 2017/5/7.
 */
@Getter
@Setter
public class MaasException extends RuntimeException {
    private String code;
    private String massage;

    public MaasException(String code,String massage) {
        this.code = code;
        this.massage = massage;
    }
}
