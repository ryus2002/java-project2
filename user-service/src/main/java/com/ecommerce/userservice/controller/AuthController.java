package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 認證控制器
 * 
 * 處理用戶註冊和登入等認證相關請求
 */
@CrossOrigin(origins = "*", maxAge = 3600)  // 允許跨域請求
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "用戶認證 API")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用戶登入
     * 
     * @param loginRequest 登入請求數據
     * @return JWT 響應
     */
    @PostMapping("/signin")
    @Operation(summary = "用戶登入", description = "使用用戶名和密碼登入系統，返回 JWT 令牌")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "登入請求數據，包含用戶名和密碼", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        
        JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    /**
     * 用戶註冊
     * 
     * @param signupRequest 註冊請求數據
     * @return 註冊結果消息
     */
    @PostMapping("/signup")
    @Operation(summary = "用戶註冊", description = "創建新用戶，並分配相應的角色")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "註冊請求數據，包含用戶名、郵箱、密碼等", required = true)
            @Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse response = userService.registerUser(signupRequest);
        return ResponseEntity.ok(response);
    }
}