package com.ecreditpal.maas.service;

import com.ecreditpal.maas.common.utils.ClassUtil;
import com.ecreditpal.maas.service.annotation.Model;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Slf4j
public class ServiceContainer {
    private static Map<String, ModelService> map = Maps.newHashMap();

    static {
        List<Class<?>> list = ClassUtil.getClasses("com.ecreditpal.maas.service");
        for (Class<?> clz : list) {
            Model model = clz.getAnnotation(Model.class);
            if (model != null) {
                try {
                    map.put(model.apiCode(), (ModelService) clz.newInstance());
                    log.info("init modelService,apiCode:{}",model.apiCode());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        log.info("init {} modelService",map.size());
    }

    /**
     * 根据apiCode获得模型的Service
     * @param apiCode ecreditpal apicode
     * @return ModelService
     */
    public static ModelService getModelService(String apiCode) {
        return map.get(apiCode);
    }
}

