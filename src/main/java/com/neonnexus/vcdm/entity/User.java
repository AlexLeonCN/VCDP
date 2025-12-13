package com.neonnexus.vcdm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password") // 排除密码字段，避免在日志中泄露
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 便捷构造函数
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

