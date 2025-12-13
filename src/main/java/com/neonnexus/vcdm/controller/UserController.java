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
}

