package com.ecommerce.userservice.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具類
 * 
 * 此類提供 JWT (JSON Web Token) 相關的工具方法，包括生成令牌、解析令牌和驗證令牌。
 * JWT 用於實現無狀態的身份驗證機制，避免在服務器端存儲會話狀態。
 */
@Component  // 標記為 Spring 組件
public class JwtUtils {
    
    // 日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // 從配置文件中注入 JWT 密鑰
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 從配置文件中注入 JWT 過期時間
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * 生成 JWT 令牌
     * 
     * 根據用戶的身份驗證信息生成 JWT 令牌
     * 
     * @param authentication 身份驗證對象，包含用戶信息
     * @return 生成的 JWT 令牌字符串
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))  // 設置令牌主題為用戶名
                .setIssuedAt(new Date())                   // 設置令牌簽發時間
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))  // 設置令牌過期時間
                .signWith(key(), SignatureAlgorithm.HS256)  // 使用 HS256 算法和密鑰簽名
                .compact();  // 生成最終的令牌字符串
    }

    /**
     * 生成用於簽名的密鑰
     * 
     * 將配置的 JWT 密鑰字符串轉換為 HMAC-SHA 密鑰
     * 
     * @return 用於簽名的密鑰對象
     */
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 從 JWT 令牌中獲取用戶名
     * 
     * @param token JWT 令牌字符串
     * @return 令牌中包含的用戶名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 驗證 JWT 令牌的有效性
     * 
     * 檢查令牌是否有效、是否過期等
     * 
     * @param authToken JWT 令牌字符串
     * @return 如果令牌有效則返回 true，否則返回 false
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            // 令牌格式不正確
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // 令牌已過期
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 不支持的令牌類型
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // 令牌為空
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}