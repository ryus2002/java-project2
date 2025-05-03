package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 用戶服務介面
 * 
 * 定義用戶相關的業務邏輯操作，包括註冊、登入、查詢用戶等功能。
 * 將業務邏輯與控制器分離，提高代碼的可維護性和可測試性。
 */
public interface UserService {
    
    /**
     * 用戶註冊
     * 
     * @param signupRequest 註冊請求數據
     * @return 包含操作結果信息的響應
     */
    MessageResponse registerUser(SignupRequest signupRequest);
    
    /**
     * 用戶登入
     * 
     * @param loginRequest 登入請求數據
     * @return 包含 JWT 令牌和用戶信息的響應
     */
    JwtResponse authenticateUser(LoginRequest loginRequest);
    
    /**
     * 根據用戶 ID 查詢用戶
     * 
     * @param id 用戶 ID
     * @return 包含用戶的 Optional 對象，如果未找到則為空
     */
    Optional<User> getUserById(Long id);
    
    /**
     * 查詢所有用戶
     * 
     * @return 用戶列表
     */
    List<User> getAllUsers();
    
    /**
     * 更新用戶信息
     * 
     * @param id 用戶 ID
     * @param user 更新後的用戶數據
     * @return 更新後的用戶對象
     */
    User updateUser(Long id, User user);
    
    /**
     * 刪除用戶
     * 
     * @param id 用戶 ID
     * @return 包含操作結果信息的響應
     */
    MessageResponse deleteUser(Long id);
}