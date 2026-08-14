package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public abstract class EcuInterface {
    private String id;
    private String projectId;
    private String ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
}
