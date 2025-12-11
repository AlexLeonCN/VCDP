package com.neonnexus.vcdm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HellowController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if ("admin".equals(username) && "123456".equals(password)) {
            return Map.of("success", true, "message", "登录成功");
        } else {
            return Map.of("success", false, "message", "用户名或密码错误");
        }
    }
}
