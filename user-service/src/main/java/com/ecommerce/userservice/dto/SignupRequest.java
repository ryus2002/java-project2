package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * 用戶註冊請求 DTO
 * 
 * 此類用於封裝用戶註冊請求的數據，並提供數據驗證功能。
 * 使用 Jakarta Bean Validation API 進行請求參數的驗證。
 */
@Data  // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
public class SignupRequest {
    
    /**
     * 用戶名
     * 
     * 不能為空，長度必須在 3 到 20 個字符之間
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    /**
     * 郵箱地址
     * 
     * 不能為空，長度必須小於 50 個字符，必須是有效的郵箱格式
     */
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * 密碼
     * 
     * 不能為空，長度必須在 6 到 40 個字符之間
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
    
    /**
     * 名字
     * 
     * 可選字段，用戶的名字
     */
    private String firstName;
    
    /**
     * 姓氏
     * 
     * 可選字段，用戶的姓氏
     */
    private String lastName;
    
    /**
     * 電話號碼
     * 
     * 可選字段，用戶的電話號碼
     */
    private String phone;
    
    /**
     * 角色列表
     * 
     * 可選字段，用戶的角色列表
     * 如果未提供，則默認為普通用戶角色
     */
    private Set<String> roles;
}