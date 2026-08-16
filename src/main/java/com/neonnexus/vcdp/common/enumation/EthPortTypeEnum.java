package com.neonnexus.vcdp.common.enumation;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum EthPortTypeEnum {
    FAST_ETHERNET(0, "百兆"),
    GIGABIT_ETHERNET(1, "千兆");

    private final int code;
    private final String label;

    EthPortTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(item -> item.code == code);
    }

    public static Optional<EthPortTypeEnum> fromCode(Integer code) {
        if (code == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(item -> item.code == code).findFirst();
    }
}
