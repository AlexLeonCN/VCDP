package com.neonnexus.vcdp.common.enumation;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MsgTypeEnum {
    StandardCAN(1, "StandardCAN"),
    StandardCAN_FD(2, "StandardCAN_FD"),
    ExtendedCAN(3, "ExtendedCAN"),
    ExtendedCAN_FD(4, "ExtendedCAN_FD"),
    LIN(5, "LIN");

    private Integer code;
    private String value;

    MsgTypeEnum(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public static boolean checkExist(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Arrays.stream(MsgTypeEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny().isPresent();
    }

    public static MsgTypeEnum getByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Optional<MsgTypeEnum> opt = Arrays.stream(MsgTypeEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny();
        if (!opt.isPresent()) {
            return null;
        }
        return opt.get();
    }
}
