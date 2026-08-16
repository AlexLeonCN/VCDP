package com.neonnexus.vcdp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花算法ID生成器
 * 
 * 64位ID结构：
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * |   |-------------------------- 41位时间戳 -------------------------|  |--5位数据中心ID--|  |--5位机器ID--|  |--12位序列号--|
 * 符号位（0，正数）
 * 
 * 时间戳：从起始时间（2024-01-01 00:00:00）开始计算，可以使用69年
 * 数据中心ID：0-31，支持32个数据中心
 * 机器ID：0-31，每个数据中心支持32台机器
 * 序列号：0-4095，同一毫秒内最多生成4096个ID
 */
@Component
public class SnowflakeIdGenerator {

    /**
     * 起始时间戳（2024-01-01 00:00:00 UTC）
     */
    private static final long EPOCH = 1704067200000L;

    /**
     * 数据中心ID占用的位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 机器ID占用的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心ID的最大值（2^5 - 1 = 31）
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 机器ID的最大值（2^5 - 1 = 31）
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列号的最大值（2^12 - 1 = 4095）
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID需要左移的位数（12位）
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID需要左移的位数（12 + 5 = 17位）
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳需要左移的位数（12 + 5 + 5 = 22位）
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 数据中心ID（Spring环境可通过配置文件注入）
     */
    @Value("${snowflake.datacenter-id:0}")
    private long datacenterId;

    /**
     * 机器ID（Spring环境可通过配置文件注入）
     */
    @Value("${snowflake.worker-id:0}")
    private long workerId;

    /**
     * 序列号
     */
    private final AtomicLong sequence = new AtomicLong(0L);

    /**
     * 上次生成ID的时间戳
     */
    private volatile long lastTimestamp = -1L;

    /**
     * 默认实例（数据中心ID=0，机器ID=0）
     */
    private static volatile SnowflakeIdGenerator defaultInstance;

    /**
     * 无参构造函数（用于Spring Bean创建，配置通过@Value字段注入）
     */
    public SnowflakeIdGenerator() {
        // 在Spring环境中，字段会通过@Value自动注入
        // 验证将在nextId()方法中首次调用时进行（延迟验证）
    }

    /**
     * 带参构造函数（用于非Spring环境或自定义配置）
     * 
     * @param datacenterId 数据中心ID（0-31）
     * @param workerId 机器ID（0-31）
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("数据中心ID必须在0和%d之间", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("机器ID必须在0和%d之间", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 获取默认实例（单例模式，用于非Spring环境）
     * 
     * @return 默认实例
     */
    public static SnowflakeIdGenerator getDefaultInstance() {
        return getDefaultInstance(0, 0);
    }

    /**
     * 获取指定数据中心ID和机器ID的实例（单例模式，用于非Spring环境）
     * 
     * @param datacenterId 数据中心ID
     * @param workerId 机器ID
     * @return 实例
     */
    public static SnowflakeIdGenerator getDefaultInstance(long datacenterId, long workerId) {
        if (defaultInstance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (defaultInstance == null) {
                    defaultInstance = new SnowflakeIdGenerator(datacenterId, workerId);
                }
            }
        }
        return defaultInstance;
    }

    /**
     * 生成下一个ID
     *
     * @return 唯一的雪花 ID（十进制字符串，避免前端 Number 精度丢失）
     */
    public String nextId() {
        return String.valueOf(nextLongId());
    }

    /**
     * 生成下一个数值型雪花 ID，仅供内部解析与测试使用。
     */
    synchronized long nextLongId() {
        // 延迟验证（适用于Spring环境，在首次使用时验证配置）
        validateIds();
        
        long timestamp = System.currentTimeMillis();

        // 如果当前时间小于上次生成ID的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回退，拒绝生成ID。上次时间戳：%d，当前时间戳：%d",
                            lastTimestamp, timestamp));
        }

        // 如果是同一毫秒内生成的
        if (timestamp == lastTimestamp) {
            // 序列号自增，并确保不超过最大值
            long currentSequence = sequence.incrementAndGet();
            if (currentSequence > MAX_SEQUENCE) {
                // 序列号溢出，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
                sequence.set(0);
                lastTimestamp = timestamp;
                return generateId(timestamp, 0);
            }
            return generateId(timestamp, currentSequence);
        } else {
            // 新的毫秒，序列号重置为0
            sequence.set(0);
            lastTimestamp = timestamp;
            return generateId(timestamp, 0);
        }
    }

    /**
     * 生成ID（核心算法）
     * 
     * @param timestamp 时间戳
     * @param sequence 序列号
     * @return ID
     */
    private long generateId(long timestamp, long sequence) {
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 新的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 从ID中解析时间戳
     * 
     * @param id 雪花算法生成的ID
     * @return 时间戳（毫秒）
     */
    public static long parseTimestamp(long id) {
        return ((id >> TIMESTAMP_LEFT_SHIFT) & (~(-1L << 41L))) + EPOCH;
    }

    /**
     * 从ID中解析数据中心ID
     * 
     * @param id 雪花算法生成的ID
     * @return 数据中心ID
     */
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 从ID中解析机器ID
     * 
     * @param id 雪花算法生成的ID
     * @return 机器ID
     */
    public static long parseWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID中解析序列号
     * 
     * @param id 雪花算法生成的ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 验证数据中心ID和机器ID是否有效
     */
    private void validateIds() {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalStateException(
                    String.format("数据中心ID必须在0和%d之间，当前值：%d", MAX_DATACENTER_ID, datacenterId));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalStateException(
                    String.format("机器ID必须在0和%d之间，当前值：%d", MAX_WORKER_ID, workerId));
        }
    }
}

