package com.neonnexus.vcdm.entity.po.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EcuForwardInfo {
    private Long id;
    private Long ecuId;
    private Long projectId;
    @JsonProperty("pFlashMemoryStartAddress")
    private String pFlashMemoryStartAddress;
    @JsonProperty("pFlashMemorySizeLimit")
    private String pFlashMemorySizeLimit;
    private String ramMemoryStartAddress;
    private String ramMemorySizeLimit;
}
