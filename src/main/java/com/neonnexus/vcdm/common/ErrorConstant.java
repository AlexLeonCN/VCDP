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

    interface LoginErr {
        Pair<Integer, String> WRONG_USER_NAME_OR_PASSWORD = Pair.of(10010001, "用户名或密码错误");
        Pair<Integer, String> USER_NAME_OR_PASSWORD_NONE_ERR = Pair.of(10010002, "用户名和密码不能为空");
    }

    interface RegisterErr {
        Pair<Integer, String> USER_NAME_NONE_ERR = Pair.of(10020001, "用户名不能为空");
        Pair<Integer, String> PASSWORD_NONE_ERR = Pair.of(10020002, "密码不能为空");
        Pair<Integer, String> EMAIL_NONE_ERR = Pair.of(10020003, "邮箱不能为空");
        Pair<Integer, String> REGISTER_EXCEPTION_ERR = Pair.of(10020004, "注册失败，请稍后重试");
        Pair<Integer, String> USER_NAME_ALREADY_EXIST = Pair.of(10020005, " 用户名已存在");
        Pair<Integer, String> EMAIL_ALREADY_EXIST = Pair.of(10020006, " 邮箱已被注册");
    }

    interface AuthErr {
        Pair<Integer, String> ROLE_ERR = Pair.of(10030001, "角色权限不足");
        Pair<Integer, String> PERMISSION_ERR = Pair.of(10030002, "权限不足");
        Pair<Integer, String> NOT_LOGIN_OR_OVER_TIME = Pair.of(10030003, "未登录或登录已过期，请重新登录");
    }
}
