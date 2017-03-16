package com.ecreditpal.maas.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.io.Serializable;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/10.
 */
public class Result<D> implements Serializable {
    private boolean success;
    private String code;
    private String message;
    private D data;

    public static <D> Result<D> wrapSuccessfulResult(D data) {
        Result<D> result = new Result<D>();
        result.data = data;
        result.success = true;
        return result;
    }


    public static <D> Result<D> wrapErrorResult(String errorCode, String message) {
        Result<D> result = new Result<D>();
        result.success = false;
        result.code = errorCode;
        result.message = message;
        return result;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
