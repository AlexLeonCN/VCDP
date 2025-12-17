package com.neonnexus.vcdm.controller;

import com.neonnexus.vcdm.common.ErrorConstant;
import com.neonnexus.vcdm.common.Result;
import com.neonnexus.vcdm.exception.VCDPException;
import com.neonnexus.vcdm.entity.po.User;
import com.neonnexus.vcdm.entity.vo.LoginInfo;
import com.neonnexus.vcdm.service.UserService;
import com.neonnexus.vcdm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/login")
    public Result<Map<String, Object>> login(@RequestBody LoginInfo loginInfo) {
        if (loginInfo.getUserName() == null || loginInfo.getPassword() == null) {
            return Result.error(ErrorConstant.LoginErr.USER_NAME_OR_PASSWORD_NONE_ERR);
        }

        // 使用数据库验证登录
        User user = userService.login(loginInfo.getUserName(), loginInfo.getPassword());
        
        if (user != null) {
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUserName());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", Map.of(
                    "id", user.getId(),
                    "username", user.getUserName(),
                    "nickname", user.getNickName() != null ? user.getNickName() : ""
            ));
            return Result.success("登录成功", data);
        } else {
            return Result.error(ErrorConstant.LoginErr.WRONG_USER_NAME_OR_PASSWORD);
        }
    }

    @PostMapping("/api/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String email = payload.get("email");
        String nickname = payload.get("nickname");

        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            return Result.error(ErrorConstant.RegisterErr.USER_NAME_NONE_ERR);
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error(ErrorConstant.RegisterErr.PASSWORD_NONE_ERR);
        }
        if (email == null || email.trim().isEmpty()) {
            return Result.error(ErrorConstant.RegisterErr.EMAIL_NONE_ERR);
        }

        try {
            // 创建用户对象
            User user = new User();
            user.setUserName(username.trim());
            user.setPassword(password);
            user.setEmail(email.trim());
            user.setNickName(nickname != null ? nickname.trim() : null);

            // 注册用户（会自动分配普通用户角色）
            User registeredUser = userService.register(user);

            // 生成 JWT Token
            String token = jwtUtil.generateToken(registeredUser.getId(), registeredUser.getUserName());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", Map.of(
                "id", registeredUser.getId(),
                "username", registeredUser.getUserName(),
                "nickname", registeredUser.getNickName() != null ? registeredUser.getNickName() : ""
            ));
            return Result.success("注册成功", data);
        } catch (VCDPException e) {
            return Result.error(e);
        } catch (Exception e) {
            return Result.error(ErrorConstant.RegisterErr.REGISTER_EXCEPTION_ERR);
        }
    }

    /**
     * 退出登录接口
     * 仅用于后端接口请求记录，JWT 无状态认证，真正的登出由前端清除 token
     */
    @PostMapping("/api/logout")
    public Result<Void> logout() {
        // 此接口仅用于记录登出请求日志
        // JWT 无状态认证，真正的登出是前端清除 token
        return Result.success("退出登录成功", null);
    }
}

