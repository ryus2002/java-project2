package com.ecommerce.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API 閘道應用程式
 * 
 * 此應用程式作為 API 閘道運行，是所有微服務的統一入口點。
 * 它負責路由請求、負載平衡、服務發現、請求限流和安全控制等功能。
 * 客戶端只需要與 API 閘道通信，而不需要直接訪問各個微服務。
 */
@SpringBootApplication  // 標記為 Spring Boot 應用程式
public class ApiGatewayApplication {
    
    /**
     * 應用程式主入口點
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}