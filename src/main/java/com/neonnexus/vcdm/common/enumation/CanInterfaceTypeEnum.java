package com.neonnexus.vcdm.common.enumation;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum CanInterfaceTypeEnum {
    CAN(0, "CAN"),
    CANFD(1, "CANFD");

    private int code;
    private String value;

     CanInterfaceTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static boolean checkExist(String value) {
         if (StringUtils.isBlank(value)) {
             return false;
         }
        return Arrays.stream(CanInterfaceTypeEnum.values()).filter(item ->
                 item.getValue().equalsIgnoreCase(value)).findAny().isPresent();
    }

    public static CanInterfaceTypeEnum getByValue(String value) {
         if (StringUtils.isBlank(value)) {
             return null;
         }
        Optional<CanInterfaceTypeEnum> opt = Arrays.stream(CanInterfaceTypeEnum.values()).filter(item ->
                item.getValue().equalsIgnoreCase(value)).findAny();
         if (!opt.isPresent()) {
             return null;
         }
         return opt.get();
    }
}
