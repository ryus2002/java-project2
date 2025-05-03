package com.ecommerce.userservice.integration;

import com.ecommerce.userservice.UserServiceApplication;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用戶服務的整合測試類
 * 
 * 使用 TestContainers 啟動 MySQL 容器進行測試，測試用戶服務與實際數據庫的交互。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class UserServiceIntegrationTest {

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

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private String baseUrl;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 清理數據庫，初始化角色和基礎 URL
     */
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
        // 清理數據庫
        userRepository.deleteAll();
        
        // 確保角色存在
        if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
        }
        
        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
    }

    /**
     * 測試用戶註冊和登入流程
     */
    @Test
    @DisplayName("應該能夠註冊新用戶並成功登入")
    void testRegisterAndLoginUser() {
        // 準備註冊請求
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");

        // 發送註冊請求
        ResponseEntity<?> signupResponse = restTemplate.postForEntity(
                baseUrl + "/auth/signup", signupRequest, Object.class);
        
        // 驗證註冊成功
        assertEquals(HttpStatus.OK, signupResponse.getStatusCode());
        
        // 驗證用戶已經保存到數據庫
        Optional<User> savedUser = userRepository.findByUsername("testuser");
        assertTrue(savedUser.isPresent());
        assertEquals("test@example.com", savedUser.get().getEmail());
        
        // 準備登入請求
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        
        // 發送登入請求
        ResponseEntity<?> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/signin", loginRequest, Object.class);
        
        // 驗證登入成功
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    }
}