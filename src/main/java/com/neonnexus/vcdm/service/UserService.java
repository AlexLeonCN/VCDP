package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.common.ErrorConstant;
import com.neonnexus.vcdm.common.VCDPException;
import com.neonnexus.vcdm.entity.po.Role;
import com.neonnexus.vcdm.entity.po.User;
import com.neonnexus.vcdm.mapper.RoleMapper;
import com.neonnexus.vcdm.mapper.UserMapper;
import com.neonnexus.vcdm.mapper.UserRoleMapper;
import com.neonnexus.vcdm.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        return userMapper.findByUserName(username);
    }

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码（明文）
     * @return 用户信息，如果验证失败返回 null
     */
    public User login(String username, String password) {
        // 先根据用户名查询用户
        User user = userMapper.findByUserName(username);
        if (user == null) {
            return null;
        }

        // 使用密码工具验证密码
        if (user.getPassword() != null && PasswordUtil.matches(password, user.getPassword())) {
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

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 如果存在返回 true
     */
    public boolean existsByUsername(String username) {
        return userMapper.existsByUserName(username) != null;
    }

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 如果存在返回 true
     */
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email) != null;
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册的用户信息
     */
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        if (existsByUsername(user.getUserName())) {
            throw new VCDPException(ErrorConstant.RegisterErr.USER_NAME_ALREADY_EXIST);
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty() && existsByEmail(user.getEmail())) {
            throw new VCDPException(ErrorConstant.RegisterErr.EMAIL_ALREADY_EXIST);
        }

        // 使用密码工具加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = PasswordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }

        // 创建用户
        userMapper.insert(user);

        // 分配默认角色（普通用户）
        Role userRole = roleMapper.findByCode("user");
        if (userRole != null) {
            userRoleMapper.assignRole(user.getId(), userRole.getId());
        }

        return user;
    }
}

