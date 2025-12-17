package com.neonnexus.vcdm.common;

import lombok.Data;

@Data
public class VCDPException extends RuntimeException {
    private Integer code;
    private String message;

    public VCDPException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public VCDPException(Pair<Integer, String> pair) {
        super(pair.getValue());
        this.code = pair.getKey();
        this.message = pair.getValue();
    }
}
