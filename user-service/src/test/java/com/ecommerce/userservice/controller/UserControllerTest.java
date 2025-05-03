package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController 的單元測試類
 * 
 * 使用 Spring Boot 的 WebMvcTest 進行控制器層的單元測試。
 * 模擬 UserService 的行為，專注於測試控制器的請求處理和響應生成。
 * 使用 WithMockUser 註解模擬已認證的用戶，測試需要認證的端點。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<User> testUsers;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 創建測試用的用戶對象
     */
    @BeforeEach
    void setUp() {
        // 創建測試用戶
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);

        // 創建角色
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.ERole.ROLE_USER);

        // 設置用戶角色
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);

        // 創建用戶列表
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");

        testUsers = Arrays.asList(testUser, adminUser);
    }

    /**
     * 測試獲取所有用戶端點
     * 
     * 測試場景：管理員訪問
     */
    @Test
    @DisplayName("管理員應該能夠獲取所有用戶")
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsersAsAdmin() throws Exception {
        // 模擬服務行為
        when(userService.getAllUsers()).thenReturn(testUsers);

        // 執行測試並驗證結果
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("admin"));
    }

    /**
     * 測試獲取所有用戶端點
     * 
     * 測試場景：普通用戶訪問
     */
    @Test
    @DisplayName("普通用戶不應該能夠獲取所有用戶")
    @WithMockUser(roles = "USER")
    void testGetAllUsersAsUser() throws Exception {
        // 執行測試並驗證結果
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    /**
     * 測試根據 ID 獲取用戶端點
     * 
     * 測試場景：用戶存在
     */
    @Test
    @DisplayName("應該能夠獲取存在的用戶")
    @WithMockUser(roles = "USER")
    void testGetUserByIdWhenUserExists() throws Exception {
        // 模擬服務行為
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // 執行測試並驗證結果
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /**
     * 測試根據 ID 獲取用戶端點
     * 
     * 測試場景：用戶不存在
     */
    @Test
    @DisplayName("當用戶不存在時，應該返回 404")
    @WithMockUser(roles = "USER")
    void testGetUserByIdWhenUserDoesNotExist() throws Exception {
        // 模擬服務行為
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // 執行測試並驗證結果
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * 測試更新用戶端點
     * 
     * 測試場景：成功更新
     */
    @Test
    @DisplayName("應該能夠成功更新用戶")
    @WithMockUser(roles = "USER")
    void testUpdateUserSuccess() throws Exception {
        // 準備測試數據
        User updatedUser = new User();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");

        // 模擬服務行為
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        // 執行測試並驗證結果
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    /**
     * 測試刪除用戶端點
     * 
     * 測試場景：管理員刪除用戶
     */
    @Test
    @DisplayName("管理員應該能夠刪除用戶")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserAsAdmin() throws Exception {
        // 模擬服務行為
        when(userService.deleteUser(1L)).thenReturn(new MessageResponse("User deleted successfully!"));

        // 執行測試並驗證結果
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully!"));
    }

    /**
     * 測試刪除用戶端點
     * 
     * 測試場景：普通用戶嘗試刪除用戶
     */
    @Test
    @DisplayName("普通用戶不應該能夠刪除用戶")
    @WithMockUser(roles = "USER")
    void testDeleteUserAsUser() throws Exception {
        // 執行測試並驗證結果
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}