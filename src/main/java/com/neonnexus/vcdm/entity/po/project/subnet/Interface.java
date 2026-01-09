package com.neonnexus.vcdm.entity.po.project.subnet;

import lombok.Data;

@Data
public class Interface {
    private Long id;
    private String netName; // 内部网段名称
    private String busName; // DBC总线名称
    private Integer port; // 内部端口 0-63
    private String desc; // 描述
}
