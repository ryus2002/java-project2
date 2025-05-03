package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.security.jwt.JwtUtils;
import com.ecommerce.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 的單元測試類
 * 
 * 使用 Spring Boot 的 WebMvcTest 進行控制器層的單元測試。
 * 模擬 UserService 的行為，專注於測試控制器的請求處理和響應生成。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 禁用 Spring Security 過濾器鏈
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtils jwtUtils;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 測試用戶註冊端點
     * 
     * 測試場景：成功註冊
     */
    @Test
    @DisplayName("應該成功註冊用戶並返回成功訊息")
    void testRegisterUserSuccess() throws Exception {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");

        // 模擬服務行為
        when(userService.registerUser(any(SignupRequest.class)))
                .thenReturn(new MessageResponse("User registered successfully!"));

        // 執行測試並驗證結果
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    /**
     * 測試用戶註冊端點
     * 
     * 測試場景：用戶名已存在
     */
    @Test
    @DisplayName("當用戶名已存在時，應該返回錯誤訊息")
    void testRegisterUserWithExistingUsername() throws Exception {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        // 模擬服務行為
        when(userService.registerUser(any(SignupRequest.class)))
                .thenReturn(new MessageResponse("Error: Username is already taken!"));

        // 執行測試並驗證結果
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    /**
     * 測試用戶登入端點
     * 
     * 測試場景：成功登入
     */
    @Test
    @DisplayName("應該成功驗證用戶並返回 JWT 令牌")
    void testAuthenticateUserSuccess() throws Exception {
        // 準備測試數據
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // 模擬服務行為
        JwtResponse jwtResponse = new JwtResponse(
                "jwt-token", 1L, "testuser", "test@example.com", Arrays.asList("ROLE_USER"));
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // 執行測試並驗證結果
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}