package com.ecommerce.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用戶服務應用程式
 * 
 * 此應用程式提供用戶相關的功能，包括用戶註冊、登入、權限管理等。
 * 它是電商平台的核心服務之一，負責管理用戶資料和身份驗證。
 * 使用 JWT (JSON Web Token) 實現無狀態的身份驗證機制。
 */
@SpringBootApplication  // 標記為 Spring Boot 應用程式
public class UserServiceApplication {
    
    /**
     * 應用程式主入口點
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}