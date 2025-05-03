package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用戶登入請求 DTO
 * 
 * 此類用於封裝用戶登入請求的數據，並提供數據驗證功能。
 * 使用 Jakarta Bean Validation API 進行請求參數的驗證。
 */
@Data  // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
public class LoginRequest {
    
    /**
     * 用戶名
     * 
     * 不能為空，用於用戶身份識別
     */
    @NotBlank(message = "Username is required")
    private String username;
    
    /**
     * 密碼
     * 
     * 不能為空，用於用戶身份驗證
     */
    @NotBlank(message = "Password is required")
    private String password;
}