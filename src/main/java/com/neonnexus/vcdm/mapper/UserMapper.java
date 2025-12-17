package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户信息
     */
    User findByUserName(@Param("userName") String userName);

    /**
     * 根据用户名和密码查询用户（用于登录验证）
     * @param userName 用户名
     * @param password 密码（加密后的）
     * @return 用户信息
     */
    User findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

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

    /**
     * 检查用户名是否存在
     * @param userName 用户名
     * @return 用户信息，如果不存在返回 null
     */
    User existsByUserName(@Param("userName") String userName);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 用户信息，如果不存在返回 null
     */
    User existsByEmail(@Param("email") String email);
}

