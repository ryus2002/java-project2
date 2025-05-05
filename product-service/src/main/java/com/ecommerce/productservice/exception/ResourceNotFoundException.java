package com.ecommerce.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 資源未找到異常
 * 
 * 當請求的資源不存在時拋出此異常。
 * 使用 @ResponseStatus 註解將此異常映射為 HTTP 404 (NOT_FOUND) 響應。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 使用指定的錯誤消息構造異常
     * 
     * @param message 錯誤消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 使用指定的錯誤消息和原因構造異常
     * 
     * @param message 錯誤消息
     * @param cause 原因
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}