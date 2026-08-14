package com.neonnexus.vcdm.entity.dto;

import com.neonnexus.vcdm.common.enumation.PduFrameTypeEnum;

import java.util.List;

public class Pdu {
    private String id;
    private String busId;
    private PduFrameTypeEnum frameType;
    private Integer msgLength;
}
