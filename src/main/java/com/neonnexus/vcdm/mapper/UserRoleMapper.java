package com.neonnexus.vcdm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 分配用户角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}

