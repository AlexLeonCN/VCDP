package com.neonnexus.vcdm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private Long id;
    private String name;
    private String code;
    private String resource;
    private String action;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 便捷构造函数
    public Permission(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

