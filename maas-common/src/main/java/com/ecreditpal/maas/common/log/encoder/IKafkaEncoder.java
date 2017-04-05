package cn.rmt.logback.kafka.encoder;

public interface IKafkaEncoder<E> {
	public String doEncode(E event);
}
