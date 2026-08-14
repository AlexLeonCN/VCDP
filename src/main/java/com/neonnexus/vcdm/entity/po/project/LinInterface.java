package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class LinInterface {
    private String id;
    private String projectId;
    private String ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
}
