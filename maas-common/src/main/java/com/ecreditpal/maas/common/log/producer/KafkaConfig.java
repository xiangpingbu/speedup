package com.ecreditpal.maas.common.log.producer;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Properties;

/**
 * @author lifeng
 * @CreateTime 2017/4/6.
 */
public abstract class KafkaConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
    private String bootstrap_severs = null;
    private final static String BOOTSTRAP_SEVERS_NAME = "bootstrap.servers";

    private String topic = null;
    public final static String TOPIC_NAME = "topic";


    private String group_id = "0";
    private final static String GROUP_ID_NAME = "group.id";

    /**
     * 在每次重试之前，producer会更新相关topic的metadata，
     * 以此进行查看新的leader是否分配好了。因为leader的选择需要一点时间，
     * 此选项指定更新metadata之前producer需要等待的时间。默认100
     */
    private Integer retry_backoff_ms = 100;
    private final static String RETRY_BACKOFF_MS = "retry.backoff.ms";
    /**
     * 连接失败时，当我们重新连接时的等待时间。这避免了客户端反复重连
     */
    private Integer reconnect_backoff_ms = 10;
    private final static String RECONNECT_BACKOFF_MS = "reconnect.backoff.ms";

    /**
     * 表示获得leader replica已经接收了数据的确认信息。
     * 这个选择时延较小同时确保了server确认接收成功。
     */
    private Integer acks = 1;
    private final static String ACKS = "acks";

    /**
     * producer可以用来缓存数据的内存大小。
     * 如果数据产生速度大于向broker发送的速度，
     * producer会阻塞或者抛出异常，取决于“block.on.buffer.full”来表明。
     */
    private Long buffer_memory =33554432L;
    private final static String BUFFER_MEMORY = "buffer.memory";

    /**
     * 设置大于0的值将使客户端重新发送任何数据，一旦这些数据发送失败。
     * 注意，这些重试与客户端接收到发送错误时的重试没有什么不同。
     * 允许重试将潜在的改变数据的顺序，如果这两个消息记录都是发送到同一个partition，
     * 则第一个消息失败第二个发送成功，则第二条消息会比第一条消息出现要早。
     */
    private Integer retries = 0;
    private final static  String RETRIES = "retries";

    /**
     * producer将试图批处理消息记录，以减少请求次数。
     * 这将改善client与server之间的性能。这项配置控制默认的批量处理消息字节数。
     * 不会试图处理大于这个字节数的消息字节数。
     * 发送到brokers的请求将包含多个批量处理，其中会包含对每个partition的一个请求。
     * 较小的批量处理数值比较少用，并且可能降低吞吐量（0则会仅用批量处理）。
     * 较大的批量处理数值将会浪费更多内存空间，这样就需要分配特定批量处理数值的内存大小。
     */
    private Integer batch_size = 16384;
    private final static String BATCH_SIZE = "batch.size";

    /**
     * producer组将会汇总任何在请求与发送之间到达的消息记录一个单独批量的请求。
     * 通常来说，这只有在记录产生速度大于发送速度的时候才能发生。
     * 然而，在某些条件下，客户端将希望降低请求的数量，甚至降低到中等负载一下。
     * 这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理。
     * 这可以认为是TCP种Nagle的算法类似。这项设置设定了批量处理的更高的延迟边界：一旦我们获得某个partition的batch.size，他将会立即发送而不顾这项设置，然而如果我们获得消息字节数比这项设置要小的多，我们需要“linger”特定的时间以获取更多的消息。
     * 这个设置默认为0，即没有延迟。设定linger.ms=5，例如，将会减少请求数目，但是同时会增加5ms的延迟。
     */
    private Long linger_ms = 0L;
    private final static String LINGER_MS = "linger.ms";

    /**
     * 当我们内存缓存用尽时，必须停止接收新消息记录或者抛出错误。
     * 默认情况下，这个设置为真，然而某些阻塞可能不值得期待，因此立即抛出错误更好。
     * 设置为false则会这样：producer会抛出一个异常错误：BufferExhaustedException， 如果记录已经发送同时缓存已满
     */
    private Boolean block_on_buffer_full  = false;
    private final static String BLOCK_ON_BUFFER_FULL = "block.on.buffer.full";



    public Map<String,Object> initParams() {
        Map<String,Object> configs = Maps.newHashMap();

        //kafka的服务节点,以分号隔开
        configs.put(BOOTSTRAP_SEVERS_NAME, bootstrap_severs);

        configs.put(RETRY_BACKOFF_MS, retry_backoff_ms.toString());
        configs.put(RECONNECT_BACKOFF_MS, reconnect_backoff_ms.toString());
        configs.put(ACKS, acks.toString());
        configs.put(BUFFER_MEMORY, buffer_memory.toString());
        configs.put(RETRIES, retries.toString());
        configs.put(BATCH_SIZE, batch_size.toString());
        configs.put(LINGER_MS, linger_ms.toString());
        configs.put(BLOCK_ON_BUFFER_FULL, block_on_buffer_full.toString());
        configs.put("key.serializer",
                "io.confluent.kafka.serializers.KafkaAvroSerializer");
        configs.put("value.serializer",
                "io.confluent.kafka.serializers.KafkaAvroSerializer");
        configs.put("schema.registry.url", "http://localhost:18081");
        return  configs;

    }

    public void setBootstrap_severs(String bootstrap_severs) {
        this.bootstrap_severs = bootstrap_severs;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setRetry_backoff_ms(Integer retry_backoff_ms) {
        this.retry_backoff_ms = retry_backoff_ms;
    }

    public void setReconnect_backoff_ms(Integer reconnect_backoff_ms) {
        this.reconnect_backoff_ms = reconnect_backoff_ms;
    }

    public void setAcks(Integer acks) {
        this.acks = acks;
    }

    public void setBuffer_memory(Long buffer_memory) {
        this.buffer_memory = buffer_memory;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public void setBatch_size(Integer batch_size) {
        this.batch_size = batch_size;
    }

    public void setLinger_ms(Long linger_ms) {
        this.linger_ms = linger_ms;
    }

    public void setBlock_on_buffer_full(Boolean block_on_buffer_full) {
        this.block_on_buffer_full = block_on_buffer_full;
    }
}
