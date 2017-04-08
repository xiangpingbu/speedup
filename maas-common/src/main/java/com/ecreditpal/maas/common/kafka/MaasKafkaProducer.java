package com.ecreditpal.maas.common.kafka;

import com.google.common.collect.Maps;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

/**
 * @author lifeng
 * @CreateTime 2017/4/6.
 */
public class MaasKafkaProducer {
    private final static Logger log = LoggerFactory.getLogger(MaasKafkaProducer.class);
    private Producer<Object, Object> producer;
    private Random random = new SecureRandom();
    private Map<String, String> map = Maps.newHashMap();

    public MaasKafkaProducer(Map<String,Object> conf) {
        init(conf);
    }


    public void init(Map<String,Object> conf) {
        log.info("Initializing MaasKafkaProducer.");
        producer = new KafkaProducer<Object, Object>(conf);
    }


    // This method must be called before the server is shutdown. Using
    // CommonDaemon to shutdown the app server cleanly.
    public void close() {
        log.info("Closing kafka producer.");
        producer.close();
    }


    public void produce(String topic, String message)
            throws KafkaProducerException {
        String key = Long.toString(random.nextLong());
        this.produce(topic, key, message);
    }

    // produce avro message
    public void produce(String topic, String key, GenericRecord avroRecord)
            throws KafkaProducerException {
        ProducerRecord<Object, Object> record = new ProducerRecord<>(topic, key,
                avroRecord);

        try {
            producer.send(record);
        } catch (Throwable e) {
            // Kafka producer lib will throw some RuntimeException, rethrow a
            // checked exception to let caller handle the error.
            throw new KafkaProducerException(
                    "Fail to commit the avro record " + avroRecord.toString()
                            + " to kafka topic " + topic + " with key " + key,
                    e);
        }
    }

    // produce regular string message or JSON based message
    private void produce(String topic, String key, String message)
            throws KafkaProducerException {
        String prefix = "";
        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                prefix + topic, key, message);

        try {
            producer.send(record);
        } catch (Throwable e) {
            // Kafka producer lib will throw some RuntimeException, rethrow a
            // checked exception to let caller handle the error.
            throw new KafkaProducerException(
                    "Fail to commit the string '" + message
                            + "' to kafka topic " + topic + " with key " + key,
                    e);
        }
    }

    public Random getRandom() {
        return random;
    }
}
