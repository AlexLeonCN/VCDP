package com.neonnexus.vcdp.entity.po.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EcuForwardInfo {
    private String id;
    private String ecuId;
    private String projectId;
    @JsonProperty("pFlashMemoryStartAddress")
    private String pFlashMemoryStartAddress;
    @JsonProperty("pFlashMemorySizeLimit")
    private String pFlashMemorySizeLimit;
    private String ramMemoryStartAddress;
    private String ramMemorySizeLimit;
}
