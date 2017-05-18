package com.ecreditpal.maas.service;

import com.ecreditpal.maas.common.utils.ClassUtil;
import com.ecreditpal.maas.service.annotation.Model;
import com.ecreditpal.maas.service.annotation.ModelApi;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Slf4j
public class ServiceContainer {
    private static Map<String, MethodClassMapping> containerMap = Maps.newHashMap();

    static {
        try {
        /*找到com.ecreditpal.maas.service下所有的类*/
            List<Class<?>> list = ClassUtil.getClasses("com.ecreditpal.maas.service");
            for (Class<?> clz : list) {
//            Model model = clz.getAnnotation(Model.class);
//            Model model = clz.getAnnotation(Model.class);
                Model model = clz.getAnnotation(Model.class);


                if (model != null) {
                    Object obj = clz.newInstance();
                    Method m[] = clz.getDeclaredMethods();
                    for (Method method : m) {
                        ModelApi modelApi = method.getAnnotation(ModelApi.class);
                        //如果
                        if (modelApi != null) {
                            containerMap.put(modelApi.apiCode(), new MethodClassMapping(obj, method));
                            log.info("init modelService,apiCode:{}", modelApi.apiCode());
                        }
                    }
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("init {} modelService", containerMap.size());
    }

    public static Object execute(String apiCode, Map<String,String> map,GenericRecord genericRecord) {
        MethodClassMapping methodClassMapping =  containerMap.get(apiCode);
        Method method = methodClassMapping.getMethod();
        Object object = methodClassMapping.getObject();
        try {
           return method.invoke(object,map,genericRecord);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}

