package com.neonnexus.vcdp.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 简单的键值对类
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    private K key;
    private V value;

    /**
     * 创建 Pair 实例
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}

