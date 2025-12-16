package com.neonnexus.vcdm.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neonnexus.vcdm.annotation.RequiresLogin;
import com.neonnexus.vcdm.annotation.RequiresPermission;
import com.neonnexus.vcdm.annotation.RequiresRole;
import com.neonnexus.vcdm.common.ErrorConstant;
import com.neonnexus.vcdm.common.Pair;
import com.neonnexus.vcdm.common.Result;
import com.neonnexus.vcdm.service.PermissionService;
import com.neonnexus.vcdm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 权限拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理 Controller 方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查是否需要登录
        RequiresLogin requiresLogin = handlerMethod.getMethodAnnotation(RequiresLogin.class);
        if (requiresLogin != null) {
            if (!checkLogin(request, response)) {
                return false;
            }
        }

        // 检查是否需要角色
        RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);
        if (requiresRole != null) {
            Long userId = getUserIdFromToken(request);
            if (userId == null || !permissionService.hasRole(userId, requiresRole.value())) {
                sendErrorResponse(response, ErrorConstant.CommonErrorCode.FORBIDDEN, ErrorConstant.AuthErr.ROLE_ERR.getKey(),
                        ErrorConstant.AuthErr.ROLE_ERR.getValue() + ": 需要角色 " + String.join(", ", requiresRole.value()));
                return false;
            }
        }

        // 检查是否需要权限
        RequiresPermission requiresPermission = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        if (requiresPermission != null) {
            Long userId = getUserIdFromToken(request);
            if (userId == null || !permissionService.hasPermission(userId, requiresPermission.value())) {
                sendErrorResponse(response, ErrorConstant.CommonErrorCode.FORBIDDEN, ErrorConstant.AuthErr.PERMISSION_ERR.getKey(),
                        ErrorConstant.AuthErr.PERMISSION_ERR.getValue() + "：需要权限 " + String.join(", ", requiresPermission.value()));
                return false;
            }
        }

        return true;
    }

    /**
     * 检查是否已登录
     */
    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = getTokenFromRequest(request);
        
        if (token == null || !jwtUtil.validateToken(token)) {
            sendErrorResponse(response, ErrorConstant.CommonErrorCode.UNAUTHORIZED,
                ErrorConstant.AuthErr.NOT_LOGIN_OR_OVER_TIME);
            return false;
        }
        
        return true;
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Authorization 头获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 从请求参数获取（备用方案）
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        
        return null;
    }

    /**
     * 从 Token 中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return null;
        }
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送错误响应（使用 ErrorConstant 中的错误定义）
     * 使用 ObjectMapper 序列化，避免手动拼接 JSON 的问题
     */
    private void sendErrorResponse(HttpServletResponse response, int status, int code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 使用统一的 Result 响应格式，使用 ErrorConstant 中的错误码
        Result<Object> result = Result.error(code, message);

        // 使用 ObjectMapper 序列化，自动处理特殊字符转义
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private void sendErrorResponse(HttpServletResponse response, int status, Pair<Integer, String> pair) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 使用统一的 Result 响应格式，使用 ErrorConstant 中的错误码
        Result<Object> result = Result.error(pair);

        // 使用 ObjectMapper 序列化，自动处理特殊字符转义
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}

