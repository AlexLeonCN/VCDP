package com.neonnexus.vcdm.controller;

import com.neonnexus.vcdm.entity.User;
import com.neonnexus.vcdm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return Map.of("success", false, "message", "用户名和密码不能为空");
        }

        // 使用数据库验证登录
        User user = userService.login(username, password);
        
        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("userInfo", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname() != null ? user.getNickname() : ""
            ));
            return result;
        } else {
            return Map.of("success", false, "message", "用户名或密码错误");
        }
    }
}

