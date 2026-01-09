package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.auth.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色代码查询角色
     * @param code 角色代码
     * @return 角色信息
     */
    Role findByCode(@Param("code") String code);

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色信息
     */
    Role findById(@Param("id") Long id);
}

