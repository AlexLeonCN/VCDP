package com.neonnexus.vcdm.util;

import org.junit.jupiter.api.Test;

/**
 * 密码工具测试类
 * 用于生成初始数据的加密密码
 */
public class PasswordUtilTest {

    @Test
    public void generatePasswords() {
        // 生成 admin 用户的密码（原始密码：alex940712）
        String adminPassword = PasswordUtil.encode("alex940712");
        System.out.println("admin 用户密码 (alex940712): " + adminPassword);
        
        // 验证生成的密码
        boolean adminMatches = PasswordUtil.matches("alex940712", adminPassword);
        System.out.println("admin 密码验证结果: " + adminMatches);
        
        // 生成 test 用户的密码（原始密码：test1234）
        String testPassword = PasswordUtil.encode("test1234");
        System.out.println("test 用户密码 (test1234): " + testPassword);
        
        // 验证生成的密码
        boolean testMatches = PasswordUtil.matches("test1234", testPassword);
        System.out.println("test 密码验证结果: " + testMatches);
    }
}

