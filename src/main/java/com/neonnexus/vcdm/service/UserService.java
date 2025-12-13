package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.entity.User;
import com.neonnexus.vcdm.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码（明文）
     * @return 用户信息，如果验证失败返回 null
     */
    public User login(String username, String password) {
        // 先根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }

        // 密码验证（当前使用明文比较，后续可以升级为 BCrypt 加密）
        // TODO: 后续应该使用 BCryptPasswordEncoder 进行密码验证
        if (user.getPassword() != null && user.getPassword().equals(password)) {
            // 验证成功，清除密码信息后返回（不返回密码字段）
            user.setPassword(null);
            return user;
        }

        return null;
    }

    /**
     * 根据 ID 查询用户
     * @param id 用户 ID
     * @return 用户信息
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建的用户信息
     */
    public User createUser(User user) {
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }
        userMapper.insert(user);
        return user;
    }
}

