package com.ecreditpal.maas.common.log;

import java.util.HashMap;
import java.util.Map;


import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import org.apache.commons.lang3.StringUtils;

public abstract class KafkaAppenderConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E>{
	public static Map<String,Object> producerConf;
	
	public static Integer error_producer_warn_num = 10;
	
	/**  **/
	private String bootstrap_severs = null;
	private final static String  BOOTSTRAP_SEVERS_NAME = "bootstrap.servers";
	
	
	private String topic = null;
	public final static String  TOPIC_NAME = "topic";
	
	
	private String group_id = "0";
	private final static String  GROUP_ID_NAME = "group.id";
	
	
	private Integer retries = 1;
	private final static String  RETRIES_NAME = "retries";
	
	
	private Integer batch_size = 16384;
	private final static String  BATCH_SIZE_NAME = "batch.size";
	
	
	private Integer linger_ms = 1;
	private final static String  LINGER_MS_NAME = "linger.ms";
	
	
	private Integer buffer_memory = 33554432;
	private final static String  BUFFER_MEMORY_NAME = "buffer.memory";
	
	
	private boolean autoFlush = true;
	public final static String  AUTO_FLUSH_NAME = "autoFlush";
	
	
	/** 序列化 **/
	public String key_serializer = "org.apache.kafka.common.serialization.StringSerializer";
	public String value_serializer = "org.apache.kafka.common.serialization.StringSerializer";
	public final static String KEY_SERIALIZER_NAME = "key.serializer";
	public final static String VALUE_SERIALIZER_NAME = "value.serializer";
	
	
	protected boolean checkPrerequisites() {
        boolean errorFree = true;

        if ( StringUtils.isBlank(bootstrap_severs ) ) {
            addError("No \"" + bootstrap_severs + "\" set for the appender named [\""
                    + name + "\"].");
            errorFree = false;
        }

        if (StringUtils.isBlank(topic) ) {
            addError("No topic set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }

        //producer config set
        {
        	producerConf = new HashMap<String,Object>();
        	producerConf.put(BOOTSTRAP_SEVERS_NAME, bootstrap_severs);
        	producerConf.put(GROUP_ID_NAME, group_id);
        	producerConf.put(TOPIC_NAME, topic);
        	producerConf.put(RETRIES_NAME, retries);
        	producerConf.put(BATCH_SIZE_NAME, batch_size);
        	producerConf.put(LINGER_MS_NAME, linger_ms);
        	producerConf.put(BUFFER_MEMORY_NAME, buffer_memory);
        	producerConf.put(AUTO_FLUSH_NAME, autoFlush);
        	producerConf.put(KEY_SERIALIZER_NAME, key_serializer);
        	producerConf.put(VALUE_SERIALIZER_NAME, value_serializer);
        }

        return errorFree;
    }


	public void setError_producer_warn_num(Integer error_producer_warn_num) {
		KafkaAppenderConfig.error_producer_warn_num = error_producer_warn_num;
	}

	public void setBootstrap_severs(String bootstrap_severs) {
		this.bootstrap_severs = bootstrap_severs;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public void setBatch_size(Integer batch_size) {
		this.batch_size = batch_size;
	}

	public void setLinger_ms(Integer linger_ms) {
		this.linger_ms = linger_ms;
	}

	public void setBuffer_memory(Integer buffer_memory) {
		this.buffer_memory = buffer_memory;
	}

	public void setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}


	
}
