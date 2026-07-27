package com.neonnexus.vcdm.common.enumation;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum CanInterfaceTypeEnum {
    CAN(0, "CAN"),
    CANFD(1, "CANFD");

    private final int code;
    private final String label;

    CanInterfaceTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(item -> item.code == code);
    }

    public static Optional<CanInterfaceTypeEnum> fromCode(Integer code) {
        if (code == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(item -> item.code == code).findFirst();
    }
}
