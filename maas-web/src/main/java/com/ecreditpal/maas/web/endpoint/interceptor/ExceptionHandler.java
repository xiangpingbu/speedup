package com.ecreditpal.maas.web.endpoint.interceptor;

import com.ecreditpal.maas.common.utils.json.JsonUtil;
import com.ecreditpal.maas.model.bean.Result;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 异常处理类,如果server中抛出运行时异常,将在这里被拦截
 * @author lifeng
 * @CreateTime 2017/5/7.
 */
@Provider
@Slf4j
public class ExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        log.error("catch an internal exception {}",e);
        Response.ResponseBuilder ResponseBuilder = null;
        if (e instanceof MaasException){
            //截取自定义类型
            MaasException exp = (MaasException) e;
            Result<String> result = Result.wrapErrorResult(exp.getCode(),exp.getMessage());
            ResponseBuilder = Response.ok(result, MediaType.APPLICATION_JSON);
        }else {
            Result result = Result.wrapErrorResult("-1",e.getMessage());
            ResponseBuilder = Response.ok(result, MediaType.APPLICATION_JSON);
        }
        return ResponseBuilder.build();
    }
}
