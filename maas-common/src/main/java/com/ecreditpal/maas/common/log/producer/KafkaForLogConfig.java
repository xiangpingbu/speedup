package com.ecreditpal.maas.common.log.producer;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.ecreditpal.maas.common.kafka.MaasKafkaConfig;
import com.google.common.collect.Maps;
import kafka.serializer.StringEncoder;
import kafka.server.KafkaConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Map;
import java.util.Properties;

/**
 * @author lifeng
 * @CreateTime 2017/4/6.
 */
@Getter
public abstract class KafkaForLogConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
    private MaasKafkaConfig maasKafkaConfig = new MaasKafkaConfig();


    public Map<String, Object> initParams() {
        MaasKafkaConfig maasKafkaConfig = new MaasKafkaConfig();
        return maasKafkaConfig.initParams();
    }
    public void setBootstrapSevers(String bootstrapSevers) {
        maasKafkaConfig.setBootstrapSevers(bootstrapSevers);
    }

    public String getTopic() {
        return maasKafkaConfig.getTopic();
    }

    public void setTopic(String topic) {
        maasKafkaConfig.setTopic(topic);
    }

    public void setGroupId(String groupId) {
        maasKafkaConfig.setGroupId(groupId);
    }


    public void setRetryBackoffMs(Integer retryBackoffMs) {
        maasKafkaConfig.setRetryBackoffMs(retryBackoffMs);
    }

    public void setReconnectBackoffMs(Integer reconnectBackoffMs) {
        maasKafkaConfig.setReconnectBackoffMs(reconnectBackoffMs);
    }


    public void setAcks(Integer acks) {
        maasKafkaConfig.setAcks(acks);
    }


    public void setBufferMemory(Long bufferMemory) {
        maasKafkaConfig.setBufferMemory(bufferMemory);
    }


    public void setRetries(Integer retries) {
        maasKafkaConfig.setRetries(retries);
    }


    public void setBatchSize(Integer batchSize) {
        maasKafkaConfig.setBatchSize(batchSize);
    }


    public void setLingerMs(Long lingerMs) {
        maasKafkaConfig.setLingerMs(lingerMs);
    }


    public void setBlockOnBufferFull(Boolean blockOnBufferFull) {
        maasKafkaConfig.setBlockOnBufferFull(blockOnBufferFull);
    }
}
