package com.neonnexus.vcdm.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 简单的键值对类
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    private K first;
    private V second;

    /**
     * 创建 Pair 实例
     */
    public static <K, V> Pair<K, V> of(K first, V second) {
        return new Pair<>(first, second);
    }
}

