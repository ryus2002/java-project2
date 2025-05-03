package com.ecommerce.userservice.integration;

import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用戶儲存庫的整合測試類
 * 
 * 使用 TestContainers 測試儲存庫層與實際數據庫的交互。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class UserRepositoryIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role userRole;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 清理數據庫，初始化角色和用戶
     */
    @BeforeEach
    void setUp() {
        // 清理數據庫
        userRepository.deleteAll();
        
        // 創建角色
        userRole = new Role();
        userRole.setName(Role.ERole.ROLE_USER);
        roleRepository.save(userRole);
        
        // 創建測試用戶
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);
        
        userRepository.save(testUser);
    }

    /**
     * 測試根據用戶名查詢用戶
     */
    @Test
    @DisplayName("應該能夠根據用戶名查詢用戶")
    void testFindByUsername() {
        // 執行測試
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // 驗證結果
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("Test", foundUser.get().getFirstName());
        assertEquals("User", foundUser.get().getLastName());
    }

    /**
     * 測試根據郵箱查詢用戶
     */
    @Test
    @DisplayName("應該能夠根據郵箱查詢用戶")
    void testFindByEmail() {
        // 執行測試
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // 驗證結果
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    /**
     * 測試檢查用戶名是否存在
     */
    @Test
    @DisplayName("應該能夠檢查用戶名是否存在")
    void testExistsByUsername() {
        // 執行測試
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");
        
        // 驗證結果
        assertTrue(exists);
        assertFalse(notExists);
    }

    /**
     * 測試檢查郵箱是否存在
     */
    @Test
    @DisplayName("應該能夠檢查郵箱是否存在")
    void testExistsByEmail() {
        // 執行測試
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");
        
        // 驗證結果
        assertTrue(exists);
        assertFalse(notExists);
    }

    /**
     * 測試創建新用戶
     */
    @Test
    @DisplayName("應該能夠創建新用戶")
    void testCreateUser() {
        // 準備測試數據
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("encodedPassword");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);
        
        // 執行測試
        User savedUser = userRepository.save(newUser);
        
        // 驗證結果
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        
        // 驗證能夠從數據庫中查詢到
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newuser", foundUser.get().getUsername());
    }

    /**
     * 測試更新用戶
     */
    @Test
    @DisplayName("應該能夠更新用戶")
    void testUpdateUser() {
        // 準備測試數據
        testUser.setFirstName("Updated");
        testUser.setLastName("Name");
        testUser.setPhone("1234567890");
        
        // 執行測試
        User updatedUser = userRepository.save(testUser);
        
        // 驗證結果
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("1234567890", updatedUser.getPhone());
        
        // 驗證能夠從數據庫中查詢到更新後的數據
        Optional<User> foundUser = userRepository.findById(testUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Updated", foundUser.get().getFirstName());
        assertEquals("Name", foundUser.get().getLastName());
        assertEquals("1234567890", foundUser.get().getPhone());
    }

    /**
     * 測試刪除用戶
     */
    @Test
    @DisplayName("應該能夠刪除用戶")
    void testDeleteUser() {
        // 執行測試
        userRepository.deleteById(testUser.getId());
        
        // 驗證結果
        Optional<User> foundUser = userRepository.findById(testUser.getId());
        assertFalse(foundUser.isPresent());
    }

    /**
     * 測試查詢所有用戶
     */
    @Test
    @DisplayName("應該能夠查詢所有用戶")
    void testFindAllUsers() {
        // 準備測試數據
        User secondUser = new User();
        secondUser.setUsername("seconduser");
        secondUser.setEmail("second@example.com");
        secondUser.setPassword("encodedPassword");
        secondUser.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        secondUser.setRoles(roles);
        
        userRepository.save(secondUser);
        
        // 執行測試
        List<User> users = userRepository.findAll();
        
        // 驗證結果
        assertEquals(2, users.size());
    }
}