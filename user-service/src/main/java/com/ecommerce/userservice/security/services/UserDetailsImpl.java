package com.ecommerce.userservice.security.services;

import com.ecommerce.userservice.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用戶詳情實現類
 * 
 * 此類實現 Spring Security 的 UserDetails 介面，
 * 用於封裝用戶的認證信息（如用戶名、密碼）和授權信息（如角色和權限）。
 * Spring Security 使用此類的實例進行身份驗證和授權決策。
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;  // 用戶 ID
    private String username;  // 用戶名
    private String email;  // 郵箱
    
    @JsonIgnore  // 在 JSON 序列化時忽略此字段，避免密碼洩露
    private String password;  // 密碼

    // 用戶的權限集合
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 構造函數
     * 
     * @param id 用戶 ID
     * @param username 用戶名
     * @param email 郵箱
     * @param password 密碼
     * @param authorities 權限集合
     */
    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * 從 User 實體構建 UserDetailsImpl 對象
     * 
     * 此方法將 User 實體中的角色轉換為 Spring Security 的 GrantedAuthority 對象，
     * 並創建一個新的 UserDetailsImpl 實例。
     * 
     * @param user User 實體
     * @return UserDetailsImpl 對象
     */
    public static UserDetailsImpl build(User user) {
        // 將 User 實體中的角色轉換為 Spring Security 的 GrantedAuthority 對象
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // 創建並返回 UserDetailsImpl 實例
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    /**
     * 獲取用戶的權限集合
     * 
     * @return 權限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 獲取用戶 ID
     * 
     * @return 用戶 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 獲取用戶郵箱
     * 
     * @return 用戶郵箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 獲取用戶密碼
     * 
     * @return 用戶密碼
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 獲取用戶名
     * 
     * @return 用戶名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 判斷帳戶是否未過期
     * 
     * @return 如果帳戶未過期則返回 true，否則返回 false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 默認帳戶永不過期
    }

    /**
     * 判斷帳戶是否未鎖定
     * 
     * @return 如果帳戶未鎖定則返回 true，否則返回 false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;  // 默認帳戶永不鎖定
    }

    /**
     * 判斷憑證是否未過期
     * 
     * @return 如果憑證未過期則返回 true，否則返回 false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 默認憑證永不過期
    }

    /**
     * 判斷帳戶是否已啟用
     * 
     * @return 如果帳戶已啟用則返回 true，否則返回 false
     */
    @Override
    public boolean isEnabled() {
        return true;  // 默認帳戶永遠啟用
    }

    /**
     * 判斷兩個對象是否相等
     * 
     * @param o 要比較的對象
     * @return 如果兩個對象相等則返回 true，否則返回 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);  // 只比較 ID 是否相等
    }

    /**
     * 計算對象的哈希碼
     * 
     * @return 對象的哈希碼
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);  // 只使用 ID 計算哈希碼
    }
}