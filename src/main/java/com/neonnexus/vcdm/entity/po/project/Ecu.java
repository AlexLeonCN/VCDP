package com.neonnexus.vcdm.entity.po.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class Ecu {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;
    private String name;
    private String type;
    private String desc;
    private String mac;
    private String ip;
    private Integer port;
    private Integer index;
}
