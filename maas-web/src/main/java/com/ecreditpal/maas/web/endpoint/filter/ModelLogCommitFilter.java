package com.ecreditpal.maas.web.endpoint.filter;

import com.ecreditpal.maas.common.WorkDispatcher;
import com.ecreditpal.maas.common.avro.LookupEventMessage.LookupEventMessage;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.common.constants.KafkaConstants;
import com.ecreditpal.maas.common.kafka.KafkaProducerException;
import com.ecreditpal.maas.common.kafka.MaasKafkaProducer;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

/**
 * @author lifeng
 * @version 2017/4/12.
 */
@Priority(Priorities.AUTHENTICATION)
public class ModelLogCommitFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModelLogCommitFilter.class);
    private static final String KAFKA_LOOKUP_EVENT_TOPIC = ConfigurationManager
            .getConfiguration().getString("kafka.lookup.event.topic",
                    KafkaConstants.KAFKA_LOOKUP_EVENT_TOPIC);

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LookupEventMessage lookupEvent = (LookupEventMessage) requestContext
                .getProperty(ModelLogFilter.PROPERTY_NAME_LOOKUP_EVENT_MESSAGE);

        if (null == lookupEvent) {
            LOGGER.debug(
                    "No lookupevent message is found. Make sure the LookupEventMessageInitFilter is called for your api.");
            return;
        }

        // TODO: Remove this logging after the system is stablized. This logging
        // could cause a lot of data in the log file.
        LOGGER.debug("Commiting the LookupEventMessage {} to kafka topic {}",
                lookupEvent, KAFKA_LOOKUP_EVENT_TOPIC);

        // Use path as the key.


        LOGGER.debug("Commiting the LookupEventMessage to kafka topic {}",
                KAFKA_LOOKUP_EVENT_TOPIC);
                    try {
                        String key = lookupEvent.getRequestInfo().getRequestPath().toString();
                        if (StringUtils.isEmpty(key)) {
                            key = "ecreditpal/rest";
                        }
                        ModelLog ModelLog = lookupEvent.getModelLog();

                        if (ModelLog != null) {
                            MaasKafkaProducer.getInstance(KAFKA_LOOKUP_EVENT_TOPIC).produce(KAFKA_LOOKUP_EVENT_TOPIC, key,
                                    lookupEvent);
                        }
                    } catch (KafkaProducerException e) {
                        LOGGER.error(
                                "Kafka service is temporarily unavailable. Failed to commit the lookup event message {}",
                                lookupEvent, e);
                    }

    }

}
