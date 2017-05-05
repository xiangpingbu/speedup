package com.ecreditpal.maas.web.endpoint.filter;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ResponseInfo;
import com.ecreditpal.maas.common.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author lifeng
 * @CreateTime 2017/4/12.
 */
@Provider
public class ModelLogResponseFilter
        implements ContainerResponseFilter {
    private static final Logger log = LoggerFactory
            .getLogger(ModelLogResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LookupEventMessage lookupEvent = (LookupEventMessage) requestContext
                .getProperty(ModelLogFilter.PROPERTY_NAME_LOOKUP_EVENT_MESSAGE);

        if (null == lookupEvent) {
            return;
        }

        log.debug("Instantiate the response info in LookupEventMessage.");
        lookupEvent.setResponseInfo(BuildResponseInfo(responseContext));
    }

    private ResponseInfo BuildResponseInfo(
            ContainerResponseContext responseContext) {
        ResponseInfo.Builder builder = ResponseInfo.newBuilder();

        builder.setResponseCode(
                responseContext.getStatusInfo().getStatusCode());
        if (null != responseContext.getEntity()) {
            try {
                builder.setResponseBody(
                        JsonUtil.toJson(responseContext.getEntity()));
            } catch (Throwable e) {
                log.error("Fail to read the response", e);
            }
        }
        return builder.build();
    }

}
