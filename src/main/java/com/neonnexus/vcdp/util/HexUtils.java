package com.neonnexus.vcdp.util;

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * 十六进制格式统一工具：统一为 0x 前缀 + 大写十六进制。
 */
public final class HexUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-F]+$");

    private HexUtils() {
    }

    /**
     * 去掉可选的 0x/0X 前缀，并转为大写十六进制正文。
     *
     * @return 不含前缀的大写十六进制；非法时返回 null
     */
    public static String stripPrefix(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String hex = trimmed.startsWith("0x") || trimmed.startsWith("0X")
                ? trimmed.substring(2)
                : trimmed;
        hex = hex.trim().toUpperCase();
        if (hex.isEmpty() || !HEX_PATTERN.matcher(hex).matches()) {
            return null;
        }
        return hex;
    }

    /**
     * 校验是否为合法十六进制字符串（可带或不带 0x/0X 前缀）。
     */
    public static boolean isValid(String value) {
        return stripPrefix(value) != null;
    }

    /**
     * 规范化十六进制字符串。
     *
     * @return 形如 0xABC 的字符串；非法时返回 null
     */
    public static String normalize(String value) {
        String hex = stripPrefix(value);
        return hex == null ? null : "0x" + hex;
    }

    /**
     * 判断规范化后的十六进制值是否大于 0。
     */
    public static boolean isPositive(String normalizedHex) {
        if (normalizedHex == null || !normalizedHex.startsWith("0x")) {
            return false;
        }
        try {
            return new BigInteger(normalizedHex.substring(2), 16).compareTo(BigInteger.ZERO) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
