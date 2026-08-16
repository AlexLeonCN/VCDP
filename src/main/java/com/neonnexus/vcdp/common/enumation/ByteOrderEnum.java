package com.neonnexus.vcdp.common.enumation;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum ByteOrderEnum {
    INTEL(0, "INTEL", "Intel"),
    MOTOROLA_MSB(1, "MOTOROLA_MSB", "Motorola_MSB"),
    MOTOROLA_LSB(2, "MOTOROLA_LSB", "Motorola_LSB");

    private int code;
    private String name;
    private String value;

    ByteOrderEnum(int code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static boolean checkExist(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Arrays.stream(ByteOrderEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny().isPresent();
    }

    public static ByteOrderEnum getByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Optional<ByteOrderEnum> opt = Arrays.stream(ByteOrderEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny();
        if (!opt.isPresent()) {
            return null;
        }
        return opt.get();
    }

}
