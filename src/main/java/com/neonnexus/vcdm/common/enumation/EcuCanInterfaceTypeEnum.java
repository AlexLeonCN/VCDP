package com.neonnexus.vcdm.common.enumation;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum EcuCanInterfaceTypeEnum {
    MCU_DIRECT(0, "MCU直连CAN"),
    LSW_HANG(1, "LSW下挂CAN");

    private final int code;
    private final String label;

    EcuCanInterfaceTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(item -> item.code == code);
    }

    public static Optional<EcuCanInterfaceTypeEnum> fromCode(Integer code) {
        if (code == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(item -> item.code == code).findFirst();
    }
}
