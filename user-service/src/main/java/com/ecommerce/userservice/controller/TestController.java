package com.ecommerce.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 測試控制器
 * 
 * 提供測試端點，用於驗證不同角色的訪問權限。
 * 這些端點可以用來測試 JWT 認證和授權機制是否正常工作。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@CrossOrigin(origins = "*", maxAge = 3600)  // 允許跨域請求
@RestController
@RequestMapping("/api/test")  // 基礎路徑
@Tag(name = "測試 API", description = "用於測試不同角色的訪問權限")
public class TestController {
    
    /**
     * 公開訪問端點
     * 
     * 所有用戶都可以訪問，無需認證。
     * 
     * @return 歡迎消息
     */
    @Operation(summary = "公開內容", description = "所有用戶都可以訪問，無需認證")
    @ApiResponse(responseCode = "200", description = "成功返回公開內容")
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    
    /**
     * 用戶角色訪問端點
     * 
     * 只有具有 USER 角色的用戶才能訪問。
     * 
     * @return 用戶內容消息
     */
    @Operation(summary = "用戶內容", description = "只有具有 USER 角色的用戶才能訪問")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回用戶內容"),
        @ApiResponse(responseCode = "401", description = "未認證"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    public String userAccess() {
        return "User Content.";
    }
    
    /**
     * 版主角色訪問端點
     * 
     * 只有具有 MODERATOR 角色的用戶才能訪問。
     * 
     * @return 版主內容消息
     */
    @Operation(summary = "版主內容", description = "只有具有 MODERATOR 角色的用戶才能訪問")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回版主內容"),
        @ApiResponse(responseCode = "401", description = "未認證"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    @SecurityRequirement(name = "JWT")
    public String moderatorAccess() {
        return "Moderator Board.";
    }
    
    /**
     * 管理員角色訪問端點
     * 
     * 只有具有 ADMIN 角色的用戶才能訪問。
     * 
     * @return 管理員內容消息
     */
    @Operation(summary = "管理員內容", description = "只有具有 ADMIN 角色的用戶才能訪問")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回管理員內容"),
        @ApiResponse(responseCode = "401", description = "未認證"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    public String adminAccess() {
        return "Admin Board.";
    }
}