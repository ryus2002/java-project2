package com.ecommerce.userservice.config;

import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 資料庫初始化器
 * 
 * 在應用程式啟動時自動初始化資料庫，創建預設的角色和管理員用戶。
 * 實現 CommandLineRunner 介面，Spring Boot 會在啟動後自動執行 run 方法。
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 應用程式啟動後執行的初始化方法
     * 
     * @param args 命令列參數
     */
    @Override
    public void run(String... args) {
        // 初始化角色
        initRoles();
        
        // 創建管理員用戶
        createAdminUser();
    }
    
    /**
     * 初始化角色
     * 
     * 創建系統中的基本角色：USER、MODERATOR 和 ADMIN
     */
    private void initRoles() {
        // 檢查角色是否已存在
        if (roleRepository.count() == 0) {
            // 創建用戶角色
            Role userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
            
            // 創建版主角色
            Role modRole = new Role();
            modRole.setName(Role.ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
            
            // 創建管理員角色
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            
            System.out.println("Roles initialized successfully!");
        }
    }
    
    /**
     * 創建管理員用戶
     * 
     * 如果管理員用戶不存在，則創建一個預設的管理員用戶
     */
    private void createAdminUser() {
        // 檢查管理員用戶是否已存在
        if (!userRepository.existsByUsername("admin")) {
            // 創建管理員用戶
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEnabled(true);
            
            // 設置管理員角色
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            // 保存管理員用戶
            userRepository.save(adminUser);
            
            System.out.println("Admin user created successfully!");
        }
    }
}