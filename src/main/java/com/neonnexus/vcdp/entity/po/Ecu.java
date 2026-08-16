package com.neonnexus.vcdp.entity.po;

import lombok.Data;

@Data
public class Ecu {
    private String id;
    private String projectId;
    private String name;
    private String type;
    private String desc;
    private String mac;
    private String ip;
    private Integer port;
    private Integer index;
}
