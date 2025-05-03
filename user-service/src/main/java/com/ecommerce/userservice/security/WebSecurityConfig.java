package com.ecommerce.userservice.security;

import com.ecommerce.userservice.security.jwt.AuthEntryPointJwt;
import com.ecommerce.userservice.security.jwt.AuthTokenFilter;
import com.ecommerce.userservice.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置類
 * 
 * 此類配置 Spring Security 的各種安全策略和認證機制。
 * 包括身份驗證提供者、密碼編碼器、安全過濾器鏈等。
 * 採用基於 JWT 的無狀態認證機制，適合微服務架構。
 */
@Configuration  // 標記為配置類
@EnableWebSecurity  // 啟用 Web 安全功能
@EnableMethodSecurity  // 啟用方法級安全性，支持 @PreAuthorize 等註解
public class WebSecurityConfig {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // 用戶詳情服務，用於加載用戶信息

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;  // 未授權處理器，處理認證失敗的情況

    /**
     * 創建 JWT 認證過濾器 Bean
     * 
     * @return JWT 認證過濾器實例
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * 創建 DAO 認證提供者 Bean
     * 
     * 此認證提供者使用 UserDetailsService 加載用戶信息，
     * 並使用 PasswordEncoder 驗證密碼。
     * 
     * @return DAO 認證提供者實例
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);  // 設置用戶詳情服務
        authProvider.setPasswordEncoder(passwordEncoder());      // 設置密碼編碼器
        
        return authProvider;
    }

    /**
     * 創建認證管理器 Bean
     * 
     * 認證管理器負責處理認證請求，如登入請求。
     * 
     * @param authConfig 認證配置
     * @return 認證管理器實例
     * @throws Exception 如果創建失敗
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 創建密碼編碼器 Bean
     * 
     * 使用 BCrypt 算法對密碼進行單向哈希，提高安全性。
     * 
     * @return 密碼編碼器實例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全過濾器鏈
     * 
     * 定義 HTTP 安全策略，包括哪些 URL 需要認證、哪些不需要等。
     * 
     * @param http HTTP 安全構建器
     * @return 配置好的安全過濾器鏈
     * @throws Exception 如果配置失敗
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // 禁用 CSRF 保護，因為使用 JWT 不需要
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))  // 設置未授權處理器
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 使用無狀態會話
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll()  // 允許所有人訪問認證相關的 URL
                    .requestMatchers("/api/test/**").permitAll()  // 允許所有人訪問測試相關的 URL
                    .anyRequest().authenticated()  // 其他所有請求都需要認證
            );
        
        http.authenticationProvider(authenticationProvider());  // 設置認證提供者
        
        // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 過濾器
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}