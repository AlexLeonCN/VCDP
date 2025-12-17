package com.neonnexus.vcdm.common;

import com.neonnexus.vcdm.exception.VCDPException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private Boolean success;
    private String message;
    private T data;

    /**
     * 成功响应（默认 code 200）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, true, "操作成功", null);
    }

    /**
     * 成功响应（带数据，默认 code 200）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, true, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据，默认 code 200）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, true, message, data);
    }

    /**
     * 失败响应（使用 ErrorConstant 中的错误定义）
     */
    public static <T> Result<T> error(Pair<Integer, String> errorPair) {
        return new Result<>(errorPair.getKey(), false, errorPair.getValue(), null);
    }

    /**
     * 失败响应（使用 ErrorConstant 中的错误定义）
     */
    public static <T> Result<T> error(VCDPException exception) {
        return new Result<>(exception.getCode(), false, exception.getMessage(), null);
    }

    /**
     * 失败响应（指定错误码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, false, message, null);
    }

    /**
     * 失败响应（指定错误码、消息和数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, false, message, data);
    }
}

