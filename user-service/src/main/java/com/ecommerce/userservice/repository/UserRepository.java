package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用戶儲存庫介面
 * 
 * 此介面繼承自 JpaRepository，提供對 User 實體的基本 CRUD 操作。
 * Spring Data JPA 會自動實現此介面，無需手動編寫實現類。
 */
@Repository  // 標記為儲存庫組件
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根據用戶名查找用戶
     * 
     * @param username 用戶名
     * @return 包含用戶的 Optional 對象，如果未找到則為空
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根據郵箱查找用戶
     * 
     * @param email 郵箱地址
     * @return 包含用戶的 Optional 對象，如果未找到則為空
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 檢查用戶名是否已存在
     * 
     * @param username 用戶名
     * @return 如果用戶名已存在則返回 true，否則返回 false
     */
    Boolean existsByUsername(String username);
    
    /**
     * 檢查郵箱是否已存在
     * 
     * @param email 郵箱地址
     * @return 如果郵箱已存在則返回 true，否則返回 false
     */
    Boolean existsByEmail(String email);
}