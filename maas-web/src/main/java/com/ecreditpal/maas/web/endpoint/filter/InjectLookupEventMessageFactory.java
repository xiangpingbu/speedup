package com.ecreditpal.maas.web.endpoint.filter;

import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author lifeng
 * @CreateTime 2017/4/12.
 */
public class InjectLookupEventMessageFactory
        implements Factory<LookupEventMessage> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(InjectLookupEventMessageFactory.class);

    private final ContainerRequestContext context;

    @Inject
    public InjectLookupEventMessageFactory(ContainerRequestContext context) {
        this.context = context;
    }

    @Override
    public LookupEventMessage provide() {
        LookupEventMessage lookUpEventMessage = (LookupEventMessage) context
                .getProperty(ModelLogFilter
                        .PROPERTY_NAME_LOOKUP_EVENT_MESSAGE);
        if (null == lookUpEventMessage) {
            LOGGER.info("No lookUpEventMessage is found.");
        }

        return lookUpEventMessage;
    }

    @Override
    public void dispose(LookupEventMessage t) {
    }
}
