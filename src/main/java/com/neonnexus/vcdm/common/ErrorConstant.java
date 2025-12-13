package com.neonnexus.vcdm.common;

/**
 * 错误常量接口
 */
public interface ErrorConstant {
    interface InnerErrorConstant {
        Pair<Integer, String> UNAUTHORIZED = Pair.of(401, "未授权");
        Pair<Integer, String> FORBIDDEN = Pair.of(403, "禁止访问");
        Pair<Integer, String> NOT_FOUND = Pair.of(404, "资源不存在");
        Pair<Integer, String> INTERNAL_ERROR = Pair.of(500, "服务器内部错误");
        Pair<Integer, String> BAD_REQUEST = Pair.of(400, "请求参数错误");
        Pair<Integer, String> UNPROCESSABLE_ENTITY = Pair.of(422, "请求实体不可处理");
        Pair<Integer, String> TOO_MANY_REQUESTS = Pair.of(429, "请求过多");
        Pair<Integer, String> SERVICE_UNAVAILABLE = Pair.of(503, "服务不可用");
        Pair<Integer, String> GATEWAY_TIMEOUT = Pair.of(504, "网关超时");  
    }
}
