package com.ecommerce.userservice.integration;

import com.ecommerce.userservice.UserServiceApplication;
import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 安全機制的整合測試類
 * 
 * 測試 JWT 認證和授權機制是否正常工作。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class SecurityIntegrationTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 清理數據庫，初始化角色和用戶，獲取令牌
     */
    @BeforeEach
    void setUp() throws Exception {
        // 清理數據庫
        userRepository.deleteAll();
        
        // 確保角色存在
        Role userRole;
        if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()) {
            userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
        } else {
            userRole = roleRepository.findByName(Role.ERole.ROLE_USER).get();
        }
        
        Role adminRole;
        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
            adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        } else {
            adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN).get();
        }
        
        // 創建普通用戶
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        
        userRepository.save(user);
        
        // 創建管理員用戶
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEnabled(true);
        
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);
        
        userRepository.save(admin);
        
        // 獲取用戶令牌
        LoginRequest userLoginRequest = new LoginRequest();
        userLoginRequest.setUsername("testuser");
        userLoginRequest.setPassword("password123");
        
        MvcResult userResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andReturn();
        
        JwtResponse userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), JwtResponse.class);
        userToken = userResponse.getToken();
        
        // 獲取管理員令牌
        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setUsername("admin");
        adminLoginRequest.setPassword("admin123");
        
        MvcResult adminResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andReturn();
        
        JwtResponse adminResponse = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(), JwtResponse.class);
        adminToken = adminResponse.getToken();
    }

    /**
     * 測試公開端點訪問
     */
    @Test
    @DisplayName("未認證用戶應該能夠訪問公開端點")
    void testPublicEndpointAccess() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public Content."));
    }

    /**
     * 測試受保護端點未認證訪問
     */
    @Test
    @DisplayName("未認證用戶不應該能夠訪問受保護端點")
    void testProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 測試用戶角色端點訪問
     */
    @Test
    @DisplayName("認證用戶應該能夠訪問用戶角色端點")
    void testUserEndpointWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/test/user")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().string("User Content."));
    }

    /**
     * 測試管理員角色端點普通用戶訪問
     */
    @Test
    @DisplayName("普通用戶不應該能夠訪問管理員角色端點")
    void testAdminEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    /**
     * 測試管理員角色端點管理員訪問
     */
    @Test
    @DisplayName("管理員應該能夠訪問管理員角色端點")
    void testAdminEndpointWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Board."));
    }

    /**
     * 測試無效令牌
     */
    @Test
    @DisplayName("無效令牌應該被拒絕")
    void testInvalidToken() throws Exception {
        mockMvc.perform(get("/api/test/user")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 測試令牌過期
     */
    @Test
    @DisplayName("過期令牌應該被拒絕")
    void testExpiredToken() throws Exception {
        // 使用一個已知的過期令牌
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxNTE2MjM5MDIyfQ.vj1GpHgYQkD_YQnO3VoRHnBZl6mYJHxqM9lKLmQ9Wn0";
        
        mockMvc.perform(get("/api/test/user")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}