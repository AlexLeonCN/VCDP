package com.neonnexus.vcdm.entity.po.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class EcuForwardInfo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ecuId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;
    @JsonProperty("pFlashMemoryStartAddress")
    private String pFlashMemoryStartAddress;
    @JsonProperty("pFlashMemorySizeLimit")
    private String pFlashMemorySizeLimit;
    private String ramMemoryStartAddress;
    private String ramMemorySizeLimit;
}
