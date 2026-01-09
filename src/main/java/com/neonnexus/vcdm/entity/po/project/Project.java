package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class Project {
    private Long id;
    private String name; // 项目名称
    private Integer topoType; // 0-集中式单部件 1-环网 2-星网
    private String desc; // 描述
}
