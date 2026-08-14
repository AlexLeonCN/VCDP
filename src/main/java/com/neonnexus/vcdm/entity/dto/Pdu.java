package com.neonnexus.vcdm.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.neonnexus.vcdm.common.enumation.PduFrameTypeEnum;

import java.util.List;

public class Pdu {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long busId;
    private PduFrameTypeEnum frameType;
    private Integer msgLength;
}
