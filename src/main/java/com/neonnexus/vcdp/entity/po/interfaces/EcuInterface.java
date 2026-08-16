package com.neonnexus.vcdp.entity.po.interfaces;

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
