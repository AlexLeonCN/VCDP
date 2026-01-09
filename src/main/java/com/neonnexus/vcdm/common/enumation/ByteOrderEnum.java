package com.neonnexus.vcdm.common.enumation;

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


}
