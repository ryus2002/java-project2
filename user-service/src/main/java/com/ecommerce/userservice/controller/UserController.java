package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * 處理用戶相關的 CRUD 操作，如查詢、更新和刪除用戶。
 * 提供 RESTful API 端點，接收客戶端的請求並返回適當的響應。
 * 使用 Spring Security 的方法級安全性控制訪問權限。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@CrossOrigin(origins = "*", maxAge = 3600)  // 允許跨域請求
@RestController
@RequestMapping("/api/users")  // 基礎路徑
@Tag(name = "用戶管理", description = "用戶 CRUD 操作的 API")
@SecurityRequirement(name = "JWT")  // 指定此控制器中的所有操作都需要 JWT 認證
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 獲取所有用戶
     * 
     * 只有具有 ADMIN 角色的用戶才能訪問此端點。
     * 
     * @return 用戶列表
     */
    @Operation(summary = "獲取所有用戶", description = "返回系統中的所有用戶列表，需要管理員權限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取用戶列表"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根據 ID 獲取用戶
     * 
     * 用戶可以訪問自己的信息，管理員可以訪問任何用戶的信息。
     * 
     * @param id 用戶 ID
     * @return 用戶信息
     */
    @Operation(summary = "根據 ID 獲取用戶", description = "返回指定 ID 的用戶信息，普通用戶只能訪問自己的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取用戶信息"),
        @ApiResponse(responseCode = "404", description = "用戶不存在"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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
     * 用戶可以更新自己的信息，管理員可以更新任何用戶的信息。
     * 
     * @param id 用戶 ID
     * @param userDetails 更新後的用戶數據
     * @return 更新後的用戶信息
     */
    @Operation(summary = "更新用戶信息", description = "更新指定 ID 的用戶信息，普通用戶只能更新自己的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新用戶信息"),
        @ApiResponse(responseCode = "404", description = "用戶不存在"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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
     * 只有具有 ADMIN 角色的用戶才能刪除用戶。
     * 
     * @param id 用戶 ID
     * @return 操作結果信息
     */
    @Operation(summary = "刪除用戶", description = "刪除指定 ID 的用戶，需要管理員權限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功刪除用戶",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "用戶不存在"),
        @ApiResponse(responseCode = "403", description = "沒有權限訪問此資源")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable Long id) {
        MessageResponse response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
}