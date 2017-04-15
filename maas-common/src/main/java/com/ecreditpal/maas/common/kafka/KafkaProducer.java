package com.ecreditpal.maas.common.kafka;

import java.util.Map;
import java.util.Objects;

/**
 * @author lifeng
 * @version 1.0 on 2017/4/5.
 */
public class KafkaProducer {
    public static final KafkaProducer instance = new KafkaProducer();

    public Map<String,Objects> config;

    private  KafkaProducer() {

    }

    public void init() {

    }


}
