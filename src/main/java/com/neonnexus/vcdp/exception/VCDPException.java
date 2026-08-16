package com.neonnexus.vcdp.exception;

import com.neonnexus.vcdp.common.Pair;

/**
 * 自定义业务异常，支持基于 ErrorConstant 中的 Pair 或错误码+信息构建。
 */
public class VCDPException extends RuntimeException {
    private final Integer code;
    private final String message;

    /**
     * 基于错误码和报错信息构建。
     */
    public VCDPException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 基于 ErrorConstant 中定义的 Pair 构建。
     */
    public VCDPException(Pair<Integer, String> pair) {
        super(pair == null ? null : pair.getValue());
        if (pair == null) {
            throw new IllegalArgumentException("error pair cannot be null");
        }
        this.code = pair.getKey();
        this.message = pair.getValue();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
