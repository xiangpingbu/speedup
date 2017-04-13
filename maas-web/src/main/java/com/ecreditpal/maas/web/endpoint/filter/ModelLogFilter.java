package com.ecreditpal.maas.web.endpoint.filter;

import com.ecreditpal.maas.common.avro.LookupEventMessage.EventIds;
import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.RequestInfo;
import com.ecreditpal.maas.common.avro.LookupEventMessage.UserInfo;
import com.ecreditpal.maas.common.utils.TaskIdHelper;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author lifeng
 * @version 2017/4/12.
 */
@Priority(1)
public class ModelLogFilter implements ContainerRequestFilter {
    public static final Logger log = LoggerFactory.getLogger(ModelLogFilter.class);
    private static final Pattern CREDENTIAL_PATTERN = Pattern
            .compile("(.*password.*|.*credential.*)");
    private static final String REQUEST_PARAM_NAME_ACCOUNT = "account";
    public static final String PROPERTY_NAME_LOOKUP_EVENT_MESSAGE = ConfigurationManager
            .getConfiguration()
            .getString("property.lookup.event", "lookup_event_message");

    @Context
    private HttpServletRequest servletRequest;

    @Context
    private Providers providers;


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!shouldApplyLookupEventMessageFilter(requestContext)) {
            return;
        }

        LookupEventMessage.Builder eventBuilder = LookupEventMessage.newBuilder();
        MultivaluedMap<String, String> form = FilterUtil.getRequestForm(requestContext,providers);

        eventBuilder.setEventId(buildEventIds(requestContext));
        eventBuilder.setRequestInfo(buildRequestInfo(requestContext, form));
        eventBuilder.setUserInfo(buildUserInfo(form));
        requestContext.setProperty(
                PROPERTY_NAME_LOOKUP_EVENT_MESSAGE,
                eventBuilder.build());
    }




    private EventIds buildEventIds(ContainerRequestContext requestContext) {
        EventIds.Builder eventIdBuilder = EventIds
                .newBuilder();
        eventIdBuilder.setEventId(TaskIdHelper.generateTaskId());
        eventIdBuilder.setIpAddress(servletRequest.getRemoteAddr());
        eventIdBuilder.setTimestampMs(System.currentTimeMillis());

        return eventIdBuilder.build();
    }




    private RequestInfo buildRequestInfo(ContainerRequestContext requestContext,
                                         MultivaluedMap<String, String> form) {
        RequestInfo.Builder requestInfoBuilder = RequestInfo
                .newBuilder();

        // TODO: we should have a style guide for all api definitions. Some
        // information may have user credentials and we should not log these
        // data.
        requestInfoBuilder.setRequestMethod(requestContext.getMethod());
        requestInfoBuilder
                .setRequestPath(requestContext.getUriInfo().getPath());
        requestInfoBuilder.setQueryParams(multiValMapToStr(
                requestContext.getUriInfo().getQueryParameters()));
        requestInfoBuilder.setPathParams(multiValMapToStr(
                requestContext.getUriInfo().getPathParameters()));
        requestInfoBuilder.setFormParams(multiValMapToStr(form));

        return requestInfoBuilder.build();
    }

    private UserInfo buildUserInfo(MultivaluedMap<String, String> form) {
        UserInfo.Builder userBuilder = UserInfo
                .newBuilder();

        if (null == form || 0 == form.size()) {
            log.debug("No user info is found in the form data. Record as annoymous user.");
            return userBuilder.build();
        }

        String userName = form.getFirst(REQUEST_PARAM_NAME_ACCOUNT);

        if (null != userName && !userName.isEmpty()) {
            userBuilder.setUserName(userName);
        }

        // Use userName to find the other user meta data.

        return userBuilder.build();
    }


    /**
     * 将map对象转换为字符串
     *
     * @param maps 一对多的map
     * @return String`
     */
    private String multiValMapToStr(final MultivaluedMap<String, String> maps) {
        StringBuilder strBuilder = new StringBuilder(512);
        for (Map.Entry<String, List<String>> entry : maps.entrySet()) {
            if (CREDENTIAL_PATTERN.matcher(entry.getKey()).matches()) {
                continue;
            }

            strBuilder.append(entry.getKey());
            strBuilder.append(":{");
            strBuilder.append(String.join(",", entry.getValue()));
            strBuilder.append("};");
        }

        return strBuilder.toString();
    }

    public static boolean shouldApplyLookupEventMessageFilter(
            ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("api-docs")) {
            log.debug(
                    "We will not generate the LookupEventMessage for path {}",
                    requestContext.getUriInfo().getPath());
            return false;
        }

        return true;
    }
}
