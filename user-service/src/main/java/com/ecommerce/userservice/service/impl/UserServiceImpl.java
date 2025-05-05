package com.ecommerce.userservice.service.impl;

import com.ecommerce.userservice.dto.JwtResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.MessageResponse;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.mapper.RoleMapper;
import com.ecommerce.userservice.mapper.UserMapper;
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
import org.springframework.security.core.GrantedAuthority;
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
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 用戶註冊
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

        // 使用映射器將註冊請求轉換為用戶實體
        User user = userMapper.toEntity(signupRequest);
        
        // 設置加密密碼
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        // 設置用戶角色
        Set<Role> roles = new HashSet<>();
        
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            // 如果沒有指定角色，默認為普通用戶
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // 根據請求中的角色名稱設置角色
            signupRequest.getRoles().forEach(roleName -> {
                Role.ERole roleEnum = roleMapper.toRoleEnum(roleName);
                if (roleEnum != null) {
                    Role role = roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
                    roles.add(role);
                }
            });
            
            // 如果沒有有效角色，默認為普通用戶
            if (roles.isEmpty()) {
                Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
    /**
     * 用戶登入
     *
     * @param loginRequest 登入請求數據
     * @return 包含 JWT 令牌和用戶信息的響應
     */
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 進行身份驗證
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 設置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 生成 JWT 令牌
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // 獲取用戶詳情
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // 獲取用戶角色
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 返回 JWT 響應
        return new JwtResponse(jwt, 
                             userDetails.getId(), 
                             userDetails.getUsername(), 
                             userDetails.getEmail(), 
                             roles);
    }

    /**
     * 根據用戶 ID 查詢用戶
     *
     * @param id 用戶 ID
     * @return 包含用戶的 Optional 對象，如果未找到則為空
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDTO);
}

    /**
     * 查詢所有用戶
     *
     * @return 用戶列表
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
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

        // 使用映射器更新用戶實體
        userMapper.updateEntity(user, userDetails);
        
        return userMapper.toDTO(userRepository.save(user));
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
            return new MessageResponse("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }
}