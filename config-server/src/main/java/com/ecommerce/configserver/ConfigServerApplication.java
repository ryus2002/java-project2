package com.ecommerce.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置中心應用程式
 * 
 * 此應用程式作為配置中心運行，負責集中管理所有微服務的配置。
 * 其他微服務會從此服務獲取自己的配置信息，實現配置的統一管理和動態更新。
 * 配置文件通常存儲在 Git 存儲庫中，支持版本控制和環境隔離。
 */
@SpringBootApplication  // 標記為 Spring Boot 應用程式
@EnableConfigServer     // 啟用配置中心功能
public class ConfigServerApplication {
    
    /**
     * 應用程式主入口點
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}