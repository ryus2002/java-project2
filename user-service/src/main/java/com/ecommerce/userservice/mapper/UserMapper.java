package com.ecommerce.userservice.mapper;

import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.security.services.UserDetailsImpl;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 用戶映射器
 * 
 * 負責在用戶實體和各種 DTO 之間進行轉換
 */
@Component
public class UserMapper {
    
    /**
     * 將註冊請求轉換為用戶實體
     * 
     * @param signupRequest 註冊請求
     * @return 用戶實體
     */
    public User toEntity(SignupRequest signupRequest) {
        if (signupRequest == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        // 密碼需要在服務層進行加密處理，這裡不直接設置
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());
        user.setEnabled(true);
        
        return user;
    }
    
    /**
     * 將用戶詳情轉換為用戶實體
     * 
     * @param userDetails 用戶詳情
     * @return 用戶實體
     */
    public User toEntity(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDetails.getId());
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        // 不設置密碼，因為 UserDetailsImpl 中的密碼已經是加密的
        
        return user;
    }
    
    /**
     * 將 User 實體轉換為 UserDTO（如果需要的話，可以創建一個 UserDTO 類）
     * 這裡示範如何將敏感信息過濾掉
     * 
     * @param user 用戶實體
     * @return 用戶 DTO
     */
    public User toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        User userDTO = new User();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        
        // 轉換角色信息，但不包含敏感數據
        if (user.getRoles() != null) {
            userDTO.setRoles(user.getRoles());
        }
        
        return userDTO;
    }
    
    /**
     * 更新用戶實體
     * 
     * @param existingUser 現有用戶實體
     * @param updatedUser 更新後的用戶數據
     */
    public void updateEntity(User existingUser, User updatedUser) {
        if (existingUser == null || updatedUser == null) {
            return;
        }
        
        // 只更新允許修改的字段
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        
        if (updatedUser.isEnabled() != existingUser.isEnabled()) {
            existingUser.setEnabled(updatedUser.isEnabled());
        }
        
        // 不更新用戶名、郵箱和密碼，這些需要特殊處理
    }
}