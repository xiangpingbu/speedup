package com.ecreditpal.maas.common.log.encoder;

public interface IKafkaEncoder<E> {
	public String doEncode(E event);
}
