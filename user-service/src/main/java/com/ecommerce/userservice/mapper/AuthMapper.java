package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.security.services.UserDetailsImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 認證映射器
 * 
 * 負責在認證相關的實體和 DTO 之間進行轉換
 */
@Component
public class AuthMapper {
    
    /**
     * 創建 JWT 響應
     * 
     * @param token JWT 令牌
     * @param userDetails 用戶詳情
     * @param roles 角色列表
     * @return JWT 響應
     */
    public JwtResponse toJwtResponse(String token, UserDetailsImpl userDetails, List<String> roles) {
        return new JwtResponse(
            token,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
        );
    }
}