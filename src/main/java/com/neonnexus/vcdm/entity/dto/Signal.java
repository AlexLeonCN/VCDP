package com.neonnexus.vcdm.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class Signal {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String signalName;
    private Integer startBit;
    private Integer bitLength;
}
