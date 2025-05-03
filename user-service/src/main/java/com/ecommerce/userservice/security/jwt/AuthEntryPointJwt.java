package com.ecommerce.userservice.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 認證入口點
 * 
 * 此類實現 AuthenticationEntryPoint 介面，用於處理未經授權的請求。
 * 當用戶嘗試訪問安全資源但未提供有效的身份驗證信息時，此類會被調用。
 * 它負責返回適當的錯誤響應，通常是 401 Unauthorized。
 */
@Component  // 標記為 Spring 組件
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // 日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * 處理未經授權的請求
     * 
     * 當用戶嘗試訪問安全資源但未提供有效的身份驗證信息時，此方法會被調用。
     * 
     * @param request HTTP 請求對象
     * @param response HTTP 響應對象
     * @param authException 身份驗證異常，包含未授權的原因
     * @throws IOException 如果發生 I/O 異常
     * @throws ServletException 如果發生 Servlet 異常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 記錄未授權錯誤
        logger.error("Unauthorized error: {}", authException.getMessage());
        
        // 返回 401 Unauthorized 響應
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}