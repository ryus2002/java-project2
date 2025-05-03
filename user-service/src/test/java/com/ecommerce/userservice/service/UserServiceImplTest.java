package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.jwt.JwtUtils;
import com.ecommerce.userservice.security.services.UserDetailsImpl;
import com.ecommerce.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 的單元測試類
 * 
 * 使用 Mockito 框架模擬依賴組件，測試 UserService 實現類的業務邏輯。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;

    /**
     * 每個測試方法執行前的準備工作
     * 
     * 創建測試用的用戶和角色對象
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
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.ERole.ROLE_USER);

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(Role.ERole.ROLE_ADMIN);

        // 設置用戶角色
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);
    }

    /**
     * 測試用戶註冊功能
     * 
     * 測試場景：成功註冊新用戶
     */
    @Test
    @DisplayName("應該成功註冊新用戶")
    void testRegisterUserSuccess() {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");

        // 模擬行為
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        // 執行測試
        MessageResponse response = userService.registerUser(signupRequest);

        // 驗證結果
        assertEquals("User registered successfully!", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * 測試用戶註冊功能
     * 
     * 測試場景：用戶名已存在
     */
    @Test
    @DisplayName("當用戶名已存在時，應該返回錯誤訊息")
    void testRegisterUserWithExistingUsername() {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("new@example.com");

        // 模擬行為
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // 執行測試
        MessageResponse response = userService.registerUser(signupRequest);

        // 驗證結果
        assertEquals("Error: Username is already taken!", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * 測試用戶註冊功能
     * 
     * 測試場景：郵箱已存在
     */
    @Test
    @DisplayName("當郵箱已存在時，應該返回錯誤訊息")
    void testRegisterUserWithExistingEmail() {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("existing@example.com");

        // 模擬行為
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // 執行測試
        MessageResponse response = userService.registerUser(signupRequest);

        // 驗證結果
        assertEquals("Error: Email is already in use!", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * 測試用戶登入功能
     * 
     * 測試場景：成功登入
     */
    @Test
    @DisplayName("應該成功驗證用戶並返回 JWT 令牌")
    void testAuthenticateUserSuccess() {
        // 準備測試數據
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // 創建 UserDetailsImpl 對象
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "encodedPassword", authorities);

        // 模擬認證過程
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        // 執行測試
        JwtResponse response = userService.authenticateUser(loginRequest);

        // 驗證結果
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(1L, response.getId());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    /**
     * 測試根據 ID 獲取用戶功能
     * 
     * 測試場景：用戶存在
     */
    @Test
    @DisplayName("當用戶存在時，應該返回用戶")
    void testGetUserByIdWhenUserExists() {
        // 模擬行為
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 執行測試
        Optional<User> result = userService.getUserById(1L);

        // 驗證結果
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    /**
     * 測試根據 ID 獲取用戶功能
     * 
     * 測試場景：用戶不存在
     */
    @Test
    @DisplayName("當用戶不存在時，應該返回空")
    void testGetUserByIdWhenUserDoesNotExist() {
        // 模擬行為
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // 執行測試
        Optional<User> result = userService.getUserById(99L);

        // 驗證結果
        assertFalse(result.isPresent());
    }

    /**
     * 測試獲取所有用戶功能
     */
    @Test
    @DisplayName("應該返回所有用戶")
    void testGetAllUsers() {
        // 準備測試數據
        List<User> users = Arrays.asList(testUser, new User());

        // 模擬行為
        when(userRepository.findAll()).thenReturn(users);

        // 執行測試
        List<User> result = userService.getAllUsers();

        // 驗證結果
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    /**
     * 測試更新用戶功能
     * 
     * 測試場景：成功更新用戶
     */
    @Test
    @DisplayName("應該成功更新用戶")
    void testUpdateUserSuccess() {
        // 準備測試數據
        User userDetails = new User();
        userDetails.setFirstName("Updated");
        userDetails.setLastName("Name");
        userDetails.setPhone("1234567890");

        // 模擬行為
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 執行測試
        User updatedUser = userService.updateUser(1L, userDetails);

        // 驗證結果
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("1234567890", updatedUser.getPhone());
    }

    /**
     * 測試刪除用戶功能
     * 
     * 測試場景：成功刪除用戶
     */
    @Test
    @DisplayName("應該成功刪除用戶")
    void testDeleteUserSuccess() {
        // 模擬行為
        when(userRepository.existsById(1L)).thenReturn(true);

        // 執行測試
        MessageResponse response = userService.deleteUser(1L);

        // 驗證結果
        assertEquals("User deleted successfully!", response.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * 測試刪除用戶功能
     * 
     * 測試場景：用戶不存在
     */
    @Test
    @DisplayName("當用戶不存在時，應該返回錯誤訊息")
    void testDeleteUserWhenUserDoesNotExist() {
        // 模擬行為
        when(userRepository.existsById(99L)).thenReturn(false);

        // 執行測試
        MessageResponse response = userService.deleteUser(99L);

        // 驗證結果
        assertEquals("Error: User not found with id: 99", response.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}