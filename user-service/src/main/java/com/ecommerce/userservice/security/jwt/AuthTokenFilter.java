package com.ecommerce.userservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 認證過濾器
 * 
 * 此過濾器用於攔截每個 HTTP 請求，檢查請求中的 JWT 令牌，
 * 如果令牌有效，則設置用戶的身份驗證信息到 Spring Security 的上下文中。
 * 繼承自 OncePerRequestFilter，確保每個請求只執行一次過濾邏輯。
 */
@Component  // 標記為 Spring 組件
public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;  // JWT 工具類，用於解析和驗證令牌

    @Autowired
    private UserDetailsService userDetailsService;  // 用戶詳情服務，用於加載用戶信息

    // 日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * 過濾器的核心方法，處理每個 HTTP 請求
     * 
     * @param request HTTP 請求對象
     * @param response HTTP 響應對象
     * @param filterChain 過濾器鏈，用於傳遞請求到下一個過濾器或目標資源
     * @throws ServletException 如果發生 Servlet 異常
     * @throws IOException 如果發生 I/O 異常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 從請求中解析 JWT 令牌
            String jwt = parseJwt(request);
            
            // 如果令牌存在且有效
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 從令牌中獲取用戶名
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 加載用戶詳情
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 創建身份驗證令牌
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 設置身份驗證詳情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 將身份驗證信息設置到 Spring Security 上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // 繼續過濾器鏈的執行
        filterChain.doFilter(request, response);
    }

    /**
     * 從 HTTP 請求中解析 JWT 令牌
     * 
     * 檢查 Authorization 頭，提取 Bearer 令牌
     * 
     * @param request HTTP 請求對象
     * @return 如果找到則返回 JWT 令牌字符串，否則返回 null
     */
    private String parseJwt(HttpServletRequest request) {
        // 獲取 Authorization 頭的值
        String headerAuth = request.getHeader("Authorization");

        // 檢查是否是 Bearer 令牌
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // 去除 "Bearer " 前綴，返回實際的令牌
            return headerAuth.substring(7);
        }

        return null;
    }
}