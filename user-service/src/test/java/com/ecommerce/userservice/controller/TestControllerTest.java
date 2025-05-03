package com.ecommerce.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TestController 的單元測試類
 * 
 * 使用 Spring Boot 的 WebMvcTest 進行控制器層的單元測試。
 * 測試不同角色訪問不同端點的權限控制。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@WebMvcTest(TestController.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 測試公開訪問端點
     * 
     * 測試場景：未認證用戶訪問
     */
    @Test
    @DisplayName("未認證用戶應該能夠訪問公開內容")
    void testAllAccess() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public Content."));
    }

    /**
     * 測試用戶角色訪問端點
     * 
     * 測試場景：具有 USER 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 USER 角色的用戶應該能夠訪問用戶內容")
    @WithMockUser(roles = "USER")
    void testUserAccessWithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Content."));
    }

    /**
     * 測試用戶角色訪問端點
     * 
     * 測試場景：具有 MODERATOR 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 MODERATOR 角色的用戶應該能夠訪問用戶內容")
    @WithMockUser(roles = "MODERATOR")
    void testUserAccessWithModeratorRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Content."));
    }

    /**
     * 測試用戶角色訪問端點
     * 
     * 測試場景：具有 ADMIN 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 ADMIN 角色的用戶應該能夠訪問用戶內容")
    @WithMockUser(roles = "ADMIN")
    void testUserAccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Content."));
    }

    /**
     * 測試用戶角色訪問端點
     * 
     * 測試場景：未認證用戶訪問
     */
    @Test
    @DisplayName("未認證用戶不應該能夠訪問用戶內容")
    void testUserAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 測試版主角色訪問端點
     * 
     * 測試場景：具有 MODERATOR 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 MODERATOR 角色的用戶應該能夠訪問版主內容")
    @WithMockUser(roles = "MODERATOR")
    void testModeratorAccessWithModeratorRole() throws Exception {
        mockMvc.perform(get("/api/test/mod"))
                .andExpect(status().isOk())
                .andExpect(content().string("Moderator Board."));
    }

    /**
     * 測試版主角色訪問端點
     * 
     * 測試場景：具有 USER 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 USER 角色的用戶不應該能夠訪問版主內容")
    @WithMockUser(roles = "USER")
    void testModeratorAccessWithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/mod"))
                .andExpect(status().isForbidden());
    }

    /**
     * 測試管理員角色訪問端點
     * 
     * 測試場景：具有 ADMIN 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 ADMIN 角色的用戶應該能夠訪問管理員內容")
    @WithMockUser(roles = "ADMIN")
    void testAdminAccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Board."));
    }

    /**
     * 測試管理員角色訪問端點
     * 
     * 測試場景：具有 USER 角色的用戶訪問
     */
    @Test
    @DisplayName("具有 USER 角色的用戶不應該能夠訪問管理員內容")
    @WithMockUser(roles = "USER")
    void testAdminAccessWithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isForbidden());
    }
}