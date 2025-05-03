package com.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * JWT 響應 DTO
 * 
 * 此類用於封裝登入成功後返回給客戶端的數據，包括 JWT 令牌和用戶基本信息。
 * 客戶端應保存 JWT 令牌，並在後續請求中使用它進行身份驗證。
 */
@Data  // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@AllArgsConstructor  // Lombok 註解，自動生成全參構造函數
public class JwtResponse {
    
    /**
     * JWT 令牌
     * 
     * 用於後續請求的身份驗證
     */
    private String token;
    
    /**
     * 令牌類型
     * 
     * 固定為 "Bearer"，符合 OAuth 2.0 規範
     */
    private String type = "Bearer";
    
    /**
     * 用戶 ID
     * 
     * 用戶的唯一標識符
     */
    private Long id;
    
    /**
     * 用戶名
     * 
     * 用戶的登入名稱
     */
    private String username;
    
    /**
     * 郵箱地址
     * 
     * 用戶的郵箱地址
     */
    private String email;
    
    /**
     * 角色列表
     * 
     * 用戶擁有的角色列表，用於前端顯示和權限控制
     */
    private List<String> roles;

    /**
     * 構造函數
     * 
     * @param token JWT 令牌
     * @param id 用戶 ID
     * @param username 用戶名
     * @param email 郵箱地址
     * @param roles 角色列表
     */
    public JwtResponse(String token, Long id, String username, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}