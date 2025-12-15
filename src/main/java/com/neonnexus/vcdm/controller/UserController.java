package com.neonnexus.vcdm.controller;

import com.neonnexus.vcdm.common.ErrorConstant;
import com.neonnexus.vcdm.common.Result;
import com.neonnexus.vcdm.entity.User;
import com.neonnexus.vcdm.service.UserService;
import com.neonnexus.vcdm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @PostMapping("/api/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return Result.error(ErrorConstant.InnerErrorConstant.BAD_REQUEST, "用户名和密码不能为空");
        }

        // 使用数据库验证登录
        User user = userService.login(username, password);
        
        if (user != null) {
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname() != null ? user.getNickname() : ""
            ));
            return Result.success("登录成功", data);
        } else {
            return Result.error(ErrorConstant.InnerErrorConstant.UNAUTHORIZED, "用户名或密码错误");
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
            return Result.error(ErrorConstant.InnerErrorConstant.BAD_REQUEST, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error(ErrorConstant.InnerErrorConstant.BAD_REQUEST, "密码不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            return Result.error(ErrorConstant.InnerErrorConstant.BAD_REQUEST, "邮箱不能为空");
        }

        try {
            // 创建用户对象
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(password);
            user.setEmail(email.trim());
            user.setNickname(nickname != null ? nickname.trim() : null);

            // 注册用户（会自动分配普通用户角色）
            User registeredUser = userService.register(user);

            // 生成 JWT Token
            String token = jwtUtil.generateToken(registeredUser.getId(), registeredUser.getUsername());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", Map.of(
                "id", registeredUser.getId(),
                "username", registeredUser.getUsername(),
                "nickname", registeredUser.getNickname() != null ? registeredUser.getNickname() : ""
            ));
            return Result.success("注册成功", data);
        } catch (RuntimeException e) {
            return Result.error(ErrorConstant.InnerErrorConstant.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return Result.error(ErrorConstant.InnerErrorConstant.INTERNAL_ERROR, "注册失败，请稍后重试");
        }
    }
}

