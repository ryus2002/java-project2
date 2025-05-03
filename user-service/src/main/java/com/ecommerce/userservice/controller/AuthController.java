package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 認證控制器
 * 
 * 處理用戶認證相關的請求，包括註冊和登入。
 * 提供 RESTful API 端點，接收客戶端的請求並返回適當的響應。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@CrossOrigin(origins = "*", maxAge = 3600)  // 允許跨域請求
@RestController
@RequestMapping("/api/auth")  // 基礎路徑
@Tag(name = "認證管理", description = "用戶註冊和登入的 API")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用戶登入端點
     * 
     * @param loginRequest 登入請求數據
     * @return 包含 JWT 令牌和用戶信息的響應
     */
    @Operation(summary = "用戶登入", description = "驗證用戶憑證並返回 JWT 令牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登入成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "用戶名或密碼錯誤",
                content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "登入請求數據，包含用戶名和密碼", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    /**
     * 用戶註冊端點
     * 
     * @param signupRequest 註冊請求數據
     * @return 包含操作結果信息的響應
     */
    @Operation(summary = "用戶註冊", description = "創建新用戶並分配角色")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "註冊成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "用戶名或郵箱已存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "註冊請求數據，包含用戶名、郵箱、密碼等", required = true)
            @Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse messageResponse = userService.registerUser(signupRequest);
        return ResponseEntity.ok(messageResponse);
    }
}