package com.ecreditpal.maas.common.kafka;

import com.ecreditpal.maas.common.log.KafkaAppenderConfig;
import com.ecreditpal.maas.common.log.producer.KafkaProducerListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/6.
 */
public class MaasKafkaProducerFactory {

        private static boolean autoFlush;
        private volatile static Integer error_times = 0;
        public static MaasKafkaProducer getProducer(Map<String, Object> conf){
            MaasKafkaProducer kafkaProducer = new MaasKafkaProducer(conf);
            return kafkaProducer;
        }

        public void start(){

        }



    }

