package com.ecreditpal.maas.common.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/9.
 */
public enum CommonTypeEnum {


    STRING(String.class),
    INTEGER(Integer.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    LONG(Long.class),
    FLOAT(Float.class);




    private static Set<Class> set = new HashSet<>();
    static{
        for (CommonTypeEnum commonTypeEnum : values()) {
            set.add(commonTypeEnum.getClassName());
        }
    }

    private Class className;
    private CommonTypeEnum(Class className) {
        this.className = className;
    }

    public Class getClassName() {
        return this.className;
    }

    public static boolean isCommonType(Object o) {
        return o != null && set.contains(o.getClass());
    }
}
