package com.neonnexus.vcdm.common;

/**
 * 错误常量接口
 */
public interface ErrorConstant {
    interface CommonErrorCode {
        Integer UNAUTHORIZED = 401;
        Integer FORBIDDEN = 403; // 禁止访问
        Integer NOT_FOUND = 404; // 资源不存在
        Integer INTERNAL_ERROR = 500; // 服务器内部错误
        Integer BAD_REQUEST = 400; // 请求参数错误
        Integer UNPROCESSABLE_ENTITY = 422; // 请求实体不可处理
        Integer TOO_MANY_REQUESTS = 429; // 请求过多
        Integer SERVICE_UNAVAILABLE = 503; // 服务不可用
        Integer GATEWAY_TIMEOUT = 504; // 网关超时"
    }

    // 错误码规则 <类型ID-1位-从1开始><模块ID-3位-从001开始><错误ID-4位-从0001开始>
}
