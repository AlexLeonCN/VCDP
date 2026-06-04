package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Project {
    private Long id;
    private String name;
    private Integer topoType;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
