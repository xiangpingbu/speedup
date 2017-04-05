package cn.rmt.logback.kafka.producer;

import java.util.Map;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import cn.rmt.logback.kafka.KafkaAppenderConfig;

public class KafkaProducerFactory {
	private static KafkaTemplate<String, String> kafkaTemplate;
	private static DefaultKafkaProducerFactory<String, String> producerFactory;
	private static boolean autoFlush;
	private static String topic;
	private volatile static Integer error_times = 0;
	public KafkaProducerFactory(Map<String, Object> conf){
		autoFlush = (boolean) conf.get( KafkaAppenderConfig.AUTO_FLUSH_NAME );
		producerFactory = new DefaultKafkaProducerFactory<String, String>(conf);
		topic = (String) conf.get(KafkaAppenderConfig.TOPIC_NAME);
	}
	
	public void start(){
		kafkaTemplate = new KafkaTemplate<>(producerFactory, autoFlush);
		kafkaTemplate.setDefaultTopic( topic );
		//添加监听
		kafkaTemplate.setProducerListener(new KafkaProducerListener());
	}
	
	public static KafkaTemplate<String, String> getKafkaTemplate(){
		return kafkaTemplate;
	}

	public static void recordError(){
		error_times = error_times+1;
	}
}
