package com.ecreditpal.maas.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/15.
 */
@Slf4j
public class ConvertUtil {
    /**
     *
     * @param clz javabean的class信息
     * @param map map对象
     * @param <T> 泛型
     * @return T
     */
    public static <T> T convertMap(Class<T> clz, Map map) {
        BeanInfo beanInfo = null;
        try {
            //获得javabean相关信息
            beanInfo = Introspector.getBeanInfo(clz);
            T obj = clz.newInstance();

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();

                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);

                    Object[] args = new Object[1];
                    args[0] = value;
                    try {
                        descriptor.getWriteMethod().invoke(obj, args);
                    } catch (InvocationTargetException e) {
                        log.error("failed to copy one property", e);
                    }
                }
            }
            return obj;
        }catch (Exception e) {
            log.error("error copying a javabean from map",e);
        }
        return null;
    }

}
