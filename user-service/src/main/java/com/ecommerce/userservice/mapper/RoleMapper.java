package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.model.Role;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色映射器
 * 
 * 負責在角色實體和角色名稱之間進行轉換
 */
@Component
public class RoleMapper {
    
    /**
     * 將角色名稱字符串轉換為角色枚舉
     * 
     * @param roleName 角色名稱字符串
     * @return 角色枚舉
     */
    public Role.ERole toRoleEnum(String roleName) {
        if (roleName == null) {
            return null;
        }
        
        try {
            return Role.ERole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 將角色名稱字符串集合轉換為角色枚舉集合
     * 
     * @param roleNames 角色名稱字符串集合
     * @return 角色枚舉集合
     */
    public Set<Role.ERole> toRoleEnums(Set<String> roleNames) {
        if (roleNames == null) {
            return new HashSet<>();
        }
        
        return roleNames.stream()
                .map(this::toRoleEnum)
                .filter(role -> role != null)
                .collect(Collectors.toSet());
    }
    
    /**
     * 將角色集合轉換為角色名稱字符串集合
     * 
     * @param roles 角色集合
     * @return 角色名稱字符串集合
     */
    public Set<String> toRoleNames(Set<Role> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}