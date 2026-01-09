package com.neonnexus.vcdm.entity.po.auth;

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
    private String userName;
    private String password;
    private String nickName;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 便捷构造函数
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}

