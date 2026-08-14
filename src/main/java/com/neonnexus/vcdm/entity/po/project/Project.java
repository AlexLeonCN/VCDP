package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Project {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
