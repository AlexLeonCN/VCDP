package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class Ecu {
    private Long id;
    private Long projectId;
    private String name;
    private String type;
    private String desc;
    private String mac;
    private String ip;
    private Integer port;
    private Integer index;
}
