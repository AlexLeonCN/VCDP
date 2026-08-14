package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class EcuLinInterface {
    private String id;
    private String projectId;
    private String ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
}
