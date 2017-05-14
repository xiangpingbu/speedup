package com.ecreditpal.maas.service;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author lifeng
 * @CreateTime 2017/5/13.
 */
@Getter
@Setter
public class MethodClassMapping {
    private Object object;
    private Method method;

    public MethodClassMapping(Object obj,Method method) {
        this.object = obj;
        this.method = method;
    }
}
