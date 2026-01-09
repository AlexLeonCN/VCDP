package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.entity.po.auth.Permission;
import com.neonnexus.vcdm.entity.po.auth.Role;
import com.neonnexus.vcdm.mapper.PermissionMapper;
import com.neonnexus.vcdm.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务类
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色代码列表
     */
    public List<String> getUserRoles(Long userId) {
        List<Role> roles = roleMapper.findByUserId(userId);
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的权限列表
     * @param userId 用户ID
     * @return 权限代码列表
     */
    public List<String> getUserPermissions(Long userId) {
        List<Permission> permissions = permissionMapper.findByUserId(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCodes 角色代码数组
     * @return 是否拥有任意一个角色
     */
    public boolean hasRole(Long userId, String... roleCodes) {
        List<String> userRoles = getUserRoles(userId);
        for (String roleCode : roleCodes) {
            if (userRoles.contains(roleCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCodes 权限代码数组
     * @return 是否拥有任意一个权限
     */
    public boolean hasPermission(Long userId, String... permissionCodes) {
        List<String> userPermissions = getUserPermissions(userId);
        for (String permissionCode : permissionCodes) {
            if (userPermissions.contains(permissionCode)) {
                return true;
            }
        }
        return false;
    }
}

