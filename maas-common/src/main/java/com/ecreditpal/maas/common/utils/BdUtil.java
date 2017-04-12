package com.ecreditpal.maas.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/4/9.
 */
public class BdUtil {

    public static <DO, BO> BO copyObj(DO request, Class<BO> cls) {
        if (null == request) return null;
        BO result;
        try {
            result = cls.newInstance();
            BeanUtils.copyProperties(request, result);
        } catch (Exception e) {
            throw new IllegalArgumentException("对象copy失败，请检查相关module", e);
        }
        return result;
    }

    public static <DO, BO> List<BO> copyObjList(List<DO> request, Class<BO> cls) {
        List<BO> result = Lists.newArrayList();
        if (request == null) return result;
        for (DO obj : request) {
            result.add(copyObj(obj, cls));
        }
        return result;
    }


    public static <DO, BO, K> Map<K, DO> copyToMap(Map<K, BO> request, Class<DO> cls) {
        Map<K, DO> result = Maps.newHashMap();
        for (Map.Entry<K, BO> item : request.entrySet()) {
            K key = item.getKey();
            BO val = item.getValue();
            result.put(key, copyObj(val, cls));
        }
        return result;
    }


}
