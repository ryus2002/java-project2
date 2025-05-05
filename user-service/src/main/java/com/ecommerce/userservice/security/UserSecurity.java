package com.ecommerce.userservice.security;

import com.ecommerce.userservice.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用戶安全檢查類
 * 
 * 提供自定義的安全檢查方法，用於在 @PreAuthorize 註解中使用
 */
@Component
public class UserSecurity {
    
    /**
     * 檢查當前用戶是否為指定 ID 的用戶
     * 
     * @param userId 用戶 ID
     * @return 如果當前用戶是指定 ID 的用戶，則返回 true；否則返回 false
     */
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof UserDetailsImpl)) {
            return false;
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        return userDetails.getId().equals(userId);
    }
}