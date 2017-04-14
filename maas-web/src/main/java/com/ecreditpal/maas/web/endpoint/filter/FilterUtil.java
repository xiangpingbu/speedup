package com.ecreditpal.maas.web.endpoint.filter;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.message.internal.MediaTypes;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
@Slf4j
public class FilterUtil {

    public static MultivaluedMap<String, String> getRequestForm(
            ContainerRequestContext requestContext, Providers providers) {
        if (!requestContext.hasEntity() || !MediaTypes.typeEqual(
                MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                requestContext.getMediaType())) {
            // Make sure all api user info are passed in through the Form
            // params.
            log.debug("No form param found.");
            return new MultivaluedHashMap<String, String>();
        }

        ByteArrayInputStream resettableIS;
        try {
            resettableIS = toResettableStream(requestContext.getEntityStream());
        } catch (IOException e1) {
            log.error("Failed to load the request form data.", e1);
            return new MultivaluedHashMap<String, String>();
        }

        MultivaluedMap<String, String> res = transFormToMap(providers,resettableIS);


        // Must call this reset. The form will not close the inputstream.
        resettableIS.reset();
        requestContext.setEntityStream(resettableIS);

        return res;
    }


    public static Map<String, String> getRequestForm(
            HttpServletRequest requestContext,Providers providers) {
        InputStream is = null;
        try {
            is = requestContext.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultivaluedMap<String, String> res = transFormToMap(providers,is);
        Map<String,String> map = Maps.newHashMapWithExpectedSize(res.size());
        //将multiMap转换为map,;默认只取list中最后一个数据
        for (Map.Entry<String, List<String>> entry : res.entrySet()) {
            List<String> list = entry.getValue();
            map.put(entry.getKey(),list.get(list.size()-1));
        }
        return map;
    }


    private static MultivaluedMap<String,String> transFormToMap(Providers providers, InputStream is) {
        try {
            Form form = providers
                    .getMessageBodyReader(Form.class, Form.class,
                            new Annotation[0],
                            MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .readFrom(Form.class, Form.class, new Annotation[0],
                            MediaType.APPLICATION_FORM_URLENCODED_TYPE, null,
                            is);
            return form.asMap();
        } catch (WebApplicationException | IOException e) {
            log.error("Failed to load the request form data..", e);
            return new MultivaluedHashMap<String, String>();
        }
    }


    @Nonnull
    private static ByteArrayInputStream toResettableStream(InputStream entityStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = entityStream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
