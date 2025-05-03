package com.ecommerce.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 服務註冊與發現中心應用程式
 * 
 * 此應用程式作為 Eureka Server 運行，負責管理所有微服務的註冊和發現。
 * 其他微服務會向此服務註冊自己，並透過此服務發現其他微服務的位置。
 */
@SpringBootApplication // 標記為 Spring Boot 應用程式
@EnableEurekaServer   // 啟用 Eureka Server 功能
public class ServiceRegistryApplication {
    
    /**
     * 應用程式主入口點
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}