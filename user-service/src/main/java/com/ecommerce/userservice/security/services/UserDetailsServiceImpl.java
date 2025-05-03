package com.ecommerce.userservice.security.services;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用戶詳情服務實現類
 * 
 * 此類實現 Spring Security 的 UserDetailsService 介面，
 * 負責從資料庫加載用戶信息，並將其轉換為 Spring Security 可用的 UserDetails 對象。
 * 這是 Spring Security 認證機制的核心組件之一。
 */
@Service  // 標記為服務組件
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;  // 用戶儲存庫，用於訪問用戶資料

    /**
     * 根據用戶名加載用戶詳情
     * 
     * 此方法從資料庫查詢用戶，並將其轉換為 UserDetails 對象。
     * 如果用戶不存在，則拋出 UsernameNotFoundException 異常。
     * 
     * @param username 用戶名
     * @return UserDetails 對象，包含用戶的認證和授權信息
     * @throws UsernameNotFoundException 如果用戶不存在
     */
    @Override
    @Transactional  // 確保方法在事務中執行
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 從資料庫查詢用戶
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // 將 User 實體轉換為 UserDetailsImpl 對象
        return UserDetailsImpl.build(user);
    }
}