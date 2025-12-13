package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户名和密码查询用户（用于登录验证）
     * @param username 用户名
     * @param password 密码（加密后的）
     * @return 用户信息
     */
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据 ID 查询用户
     * @param id 用户 ID
     * @return 用户信息
     */
    User findById(@Param("id") Long id);

    /**
     * 插入新用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);

    /**
     * 根据 ID 删除用户
     * @param id 用户 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}

