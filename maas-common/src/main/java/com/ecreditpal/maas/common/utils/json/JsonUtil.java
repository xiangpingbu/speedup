package com.ecreditpal.maas.common.utils.json;

import com.ecreditpal.maas.common.utils.json.JsonSerializeException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author luohaiyan
 * @version 2017/4/12.
 */
public class JsonUtil {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static Gson gson = new Gson();

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        // 默认时间格式
        MAPPER.setDateFormat(format);
    }

    public static String toJson(Object obj) {
        try {
            return null == obj ? null : MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> T fromJson(String string, Class<T> clazz) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, clazz);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> List<T> fromJsonArray(String string, Class<T> clazz) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> List<T> fromJsonArray(String string, TypeReference<List<T>> type) {
        try {
            return StringUtils.isEmpty(string) ? null : MAPPER.readValue(string, type);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        try {
            return null == bytes || bytes.length == 0 ? null : MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new JsonSerializeException(e);
        }
    }

    //将json 对象转为Map
    public static <T>Map<String,T> json2Map(String jsondata){

//        ObjectMapper mapper = new ObjectMapper();
//        HashMap<String,String> map = new HashMap<>();

        return gson.fromJson(jsondata,new TypeToken<HashMap<String,T>>() {
            }.getType());
    }
}
