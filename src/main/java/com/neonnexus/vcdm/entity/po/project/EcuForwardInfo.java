package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class EcuForwardInfo {
    private Long id;
    private Long ecuId;
    private Long projectId;
    private String pFlashMemoryStartAddress;
    private String pFlashMemorySizeLimit;
    private String ramMemoryStartAddress;
    private String ramMemorySizeLimit;
}
