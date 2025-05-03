package com.ecommerce.userservice.integration;

import com.ecommerce.userservice.UserServiceApplication;
import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用戶控制器的整合測試類
 * 
 * 使用 MockMvc 和 TestContainers 測試控制器與數據庫的交互。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class UserControllerIntegrationTest {

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

    private User testUser;
    private User adminUser;
    private String userToken;
    private String adminToken;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 清理數據庫，初始化角色和用戶
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
        
        // 創建測試用戶
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        testUser.setRoles(userRoles);
        
        userRepository.save(testUser);
        
        // 創建管理員用戶
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEnabled(true);
        
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        
        userRepository.save(adminUser);
        
        // 獲取用戶令牌
        LoginRequest userLoginRequest = new LoginRequest();
        userLoginRequest.setUsername("testuser");
        userLoginRequest.setPassword("password123");
        
        MvcResult userResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
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
                .andExpect(status().isOk())
                .andReturn();
        
        JwtResponse adminResponse = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(), JwtResponse.class);
        adminToken = adminResponse.getToken();
    }

    /**
     * 測試用戶註冊功能
     */
    @Test
    @DisplayName("應該成功註冊新用戶")
    void testRegisterUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    /**
     * 測試用戶登入功能
     */
    @Test
    @DisplayName("應該成功登入並返回 JWT 令牌")
    void testLoginUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /**
     * 測試獲取所有用戶功能
     */
    @Test
    @DisplayName("管理員應該能夠獲取所有用戶")
    void testGetAllUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * 測試普通用戶訪問獲取所有用戶功能
     */
    @Test
    @DisplayName("普通用戶不應該能夠獲取所有用戶")
    void testGetAllUsersAsUser() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    /**
     * 測試根據 ID 獲取用戶功能
     */
    @Test
    @DisplayName("應該能夠獲取指定 ID 的用戶")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /**
     * 測試更新用戶功能
     */
    @Test
    @DisplayName("應該能夠更新用戶信息")
    void testUpdateUser() throws Exception {
        User updateUser = new User();
        updateUser.setFirstName("Updated");
        updateUser.setLastName("Name");
        updateUser.setPhone("1234567890");
        
        mockMvc.perform(put("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    /**
     * 測試刪除用戶功能
     */
    @Test
    @DisplayName("管理員應該能夠刪除用戶")
    void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully!"));
    }

    /**
     * 測試普通用戶嘗試刪除用戶
     */
    @Test
    @DisplayName("普通用戶不應該能夠刪除用戶")
    void testDeleteUserAsUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}