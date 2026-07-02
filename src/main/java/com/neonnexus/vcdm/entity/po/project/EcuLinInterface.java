package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class EcuLinInterface {
    private Long id;
    private Long projectId;
    private Long ecuId;
    private String interfaceName;
    private Integer channelId;
}
