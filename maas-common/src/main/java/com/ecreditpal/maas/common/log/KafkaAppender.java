package com.ecreditpal.maas.common.log;

/**
 * @author lifeng
 * @CreateTime 2017/4/5.
 */
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.ecreditpal.maas.common.avro.LookupEventMessage.ModelLog;
import com.ecreditpal.maas.common.kafka.KafkaProducerException;
import com.ecreditpal.maas.common.kafka.MaasKafkaProducer;
import com.ecreditpal.maas.common.log.encoder.IKafkaEncoder;
import com.ecreditpal.maas.common.log.producer.KafkaForLogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaAppender<E> extends KafkaForLogConfig<E> {
    private final static Logger log = LoggerFactory.getLogger(KafkaAppender.class);
    private static ExecutorService exec = Executors.newFixedThreadPool(10);
    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    private IKafkaEncoder<E> encoder = null;
    private MaasKafkaProducer producer;
    //完整的一条logs数据  es的field名称
    public static String MSG_FULL_DATA_KEY = "DATA";
    public KafkaAppender(){
    }

    public void setEncoder(IKafkaEncoder<E> layout) {
        this.encoder = layout;
    }


    @Override
    public void start() {
        Map<String,Object> conf  =initParams();
        //加载初始化参数
        if( conf == null){
            addError("kafka appender 初始化参数加载失败...");
        }
        producer = new MaasKafkaProducer(conf);
        super.start();
    }

    @Override
    public void addAppender(Appender<E> newAppender) {
        aai.addAppender(newAppender);
    }


    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        return aai.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        return aai.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }

    @Override
    protected void append(E e) {
         String payload = encoder.doEncode(e);

        //发送消息到kafka
        exec.execute( new Runnable() {
            @Override
            public void run() {
                try {
                    ModelLog modelLog = new ModelLog();
                    modelLog.setModelResult(payload);
//                    modelLog.setVariableResult("");
                    producer.produce(getTopic(),"", modelLog);
//                    producer.produce(getTopic(),payload);
                } catch (KafkaProducerException e1) {
                    addError("error occured while produce log message");
                }
            }
        });
    }


}
