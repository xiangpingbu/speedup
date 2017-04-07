package com.ecreditpal.maas.common.kafka;

/**
 * @author lifeng
 * @CreateTime 2017/4/6.
 */
public class KafkaProducerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 8403642372900196938L;

    public KafkaProducerException(Throwable e) {
        super("Kafka produce got some errors, the message may "
                + "not be committed to the kafka server.", e);
    }

    public KafkaProducerException(final String error, Throwable e) {
        super(error, e);
    }

}
