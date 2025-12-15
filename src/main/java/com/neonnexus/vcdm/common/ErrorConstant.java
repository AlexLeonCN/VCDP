package com.neonnexus.vcdm.common;

/**
 * 错误常量接口
 */
public interface ErrorConstant {
    interface CommonError {
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

    // 错误码规则 <类型ID-1位-从1开始><模块ID-3位-从001开始><错误ID-4位-从0001开始>

    interface LoginRegisterErr {
        Pair<Integer, String> WRONG_USER_NAME_OR_PASSWORD = Pair.of(10010001, "用户名或密码错误");
        Pair<Integer, String> USER_NAME_OR_PASSWORD_NONE_ERR = Pair.of(10010002, "用户名和密码不能为空");
        Pair<Integer, String> USER_NAME_NONE_ERR = Pair.of(10010003, "用户名不能为空");
        Pair<Integer, String> PASSWORD_NONE_ERR = Pair.of(10010004, "密码不能为空");
        Pair<Integer, String> EMAIL_NONE_ERR = Pair.of(10010005, "邮箱不能为空");
        Pair<Integer, String> REGISTER_EXCEPTION_ERR = Pair.of(10010006, "注册失败，请稍后重试");
    }
}
