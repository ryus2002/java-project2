package com.ecommerce.userservice.service.impl;

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
import com.ecommerce.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用戶服務實現類
 * 
 * 實現 UserService 介面，提供用戶相關的業務邏輯操作。
 * 處理用戶註冊、登入、查詢、更新和刪除等功能。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用戶註冊
     * 
     * 檢查用戶名和郵箱是否已存在，如果不存在則創建新用戶。
     * 為用戶分配角色，如果請求中未指定角色，則默認為普通用戶角色。
     * 
     * @param signupRequest 註冊請求數據
     * @return 包含操作結果信息的響應
     */
    @Override
    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        // 檢查郵箱是否已存在
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // 創建新用戶
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());

        // 設置用戶角色
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // 如果未指定角色，則默認為普通用戶角色
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // 根據請求中的角色名稱查找對應的角色
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(Role.ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    /**
     * 用戶登入
     * 
     * 驗證用戶提供的憑證，如果有效則生成 JWT 令牌。
     * 
     * @param loginRequest 登入請求數據
     * @return 包含 JWT 令牌和用戶信息的響應
     */
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 驗證用戶憑證
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 設置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 生成 JWT 令牌
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // 獲取用戶詳情
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // 獲取用戶角色列表
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 返回 JWT 響應
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    /**
     * 根據用戶 ID 查詢用戶
     * 
     * @param id 用戶 ID
     * @return 包含用戶的 Optional 對象，如果未找到則為空
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 查詢所有用戶
     * 
     * @return 用戶列表
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 更新用戶信息
     * 
     * @param id 用戶 ID
     * @param userDetails 更新後的用戶數據
     * @return 更新後的用戶對象
     */
    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 更新用戶信息
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        
        // 如果提供了新密碼，則更新密碼
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    /**
     * 刪除用戶
     * 
     * @param id 用戶 ID
     * @return 包含操作結果信息的響應
     */
    @Override
    @Transactional
    public MessageResponse deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return new MessageResponse("Error: User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully!");
    }
}