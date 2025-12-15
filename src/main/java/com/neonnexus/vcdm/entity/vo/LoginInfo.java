package com.neonnexus.vcdm.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginInfo {
    /**
     * 用户名
     */
    @JsonProperty("username")
    private String userName;

    /**
     * 密码
     */
    @JsonProperty("password")
    private String password;
}
