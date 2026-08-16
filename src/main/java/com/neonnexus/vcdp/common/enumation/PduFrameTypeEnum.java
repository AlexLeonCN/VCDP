package com.neonnexus.vcdp.common.enumation;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum PduFrameTypeEnum {
    CAN(0, "CAN"),
    CANFD(1, "CANFD"),
    LIN(2, "LIN"),
    ETH(3, "ETH"),
    COM(4, "COM"), // 信号路由
    RTE(5, "RTE"); // 应用

    private Integer code;
    private String value;

    PduFrameTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static boolean checkExist(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Arrays.stream(PduFrameTypeEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny().isPresent();
    }

    public static PduFrameTypeEnum getByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Optional<PduFrameTypeEnum> opt = Arrays.stream(PduFrameTypeEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny();
        if (!opt.isPresent()) {
            return null;
        }
        return opt.get();
    }

}
