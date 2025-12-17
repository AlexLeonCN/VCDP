package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限 Mapper 接口
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限代码查询权限
     * @param code 权限代码
     * @return 权限信息
     */
    Permission findByCode(@Param("code") String code);
}

