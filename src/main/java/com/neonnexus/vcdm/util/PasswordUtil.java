package com.neonnexus.vcdm.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用 SHA-256 + 盐值的方式加密密码
 * 不依赖 Spring Security，轻量级实现
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 盐值长度（字节）
    private static final String DELIMITER = ":"; // 分隔符

    /**
     * 加密密码
     * 格式：salt:hash
     * @param password 明文密码
     * @return 加密后的密码（Base64编码）
     */
    public static String encode(String password) {
        try {
            // 生成随机盐值
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // 计算哈希值
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // 组合盐值和哈希值，使用 Base64 编码
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + DELIMITER + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 分离盐值和哈希值
            String[] parts = encodedPassword.split(DELIMITER, 2);
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            // 使用相同的盐值计算输入密码的哈希值
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] computedHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            // 比较哈希值
            return MessageDigest.isEqual(storedHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用固定盐值生成密码（仅用于初始化数据）
     * @param password 明文密码
     * @param saltBytes 固定的盐值字节数组
     * @return 加密后的密码
     */
    public static String encodeWithFixedSalt(String password, byte[] saltBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(saltBytes);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            String saltBase64 = Base64.getEncoder().encodeToString(saltBytes);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + DELIMITER + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 主方法：用于生成初始数据的加密密码
     * 使用方法：运行此类，将输出的哈希值复制到 data.sql 中
     */
    public static void main(String[] args) {
        // 使用固定盐值生成，确保每次运行结果一致
        // admin 用户的固定盐值
        byte[] adminSalt = "adminSalt2024VCDP".getBytes(StandardCharsets.UTF_8);
        String adminPassword = encodeWithFixedSalt("alex940712", adminSalt);
        System.out.println("admin 用户密码 (alex940712): " + adminPassword);
        System.out.println("验证结果: " + matches("alex940712", adminPassword));
        
        // test 用户的固定盐值
        byte[] testSalt = "testSalt2024VCDP12".getBytes(StandardCharsets.UTF_8);
        String testPassword = encodeWithFixedSalt("test1234", testSalt);
        System.out.println("\ntest 用户密码 (test1234): " + testPassword);
        System.out.println("验证结果: " + matches("test1234", testPassword));
    }
}

