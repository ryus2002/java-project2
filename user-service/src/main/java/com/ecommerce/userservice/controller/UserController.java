package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用戶控制器
 * 
 * 處理用戶管理相關請求
 */
@CrossOrigin(origins = "*", maxAge = 3600)  // 允許跨域請求
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "用戶管理 API")
@SecurityRequirement(name = "bearerAuth")  // 指定 Swagger UI 中使用的安全方案
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 獲取所有用戶
     * 
     * @return 用戶列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理員可以訪問
    @Operation(summary = "獲取所有用戶", description = "返回系統中的所有用戶列表，需要管理員權限")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根據 ID 獲取用戶
     * 
     * @param id 用戶 ID
     * @return 用戶信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")  // 管理員或當前用戶可以訪問
    @Operation(summary = "根據 ID 獲取用戶", description = "返回指定 ID 的用戶信息，需要管理員權限或是當前用戶")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable Long id) {
        
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新用戶信息
     * 
     * @param id 用戶 ID
     * @param userDetails 更新後的用戶數據
     * @return 更新後的用戶信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")  // 管理員或當前用戶可以訪問
    @Operation(summary = "更新用戶信息", description = "更新指定 ID 的用戶信息，需要管理員權限或是當前用戶")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "更新後的用戶數據", required = true)
            @RequestBody User userDetails) {
        
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 刪除用戶
     * 
     * @param id 用戶 ID
     * @return 刪除結果消息
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理員可以刪除用戶
    @Operation(summary = "刪除用戶", description = "刪除指定 ID 的用戶，需要管理員權限")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}