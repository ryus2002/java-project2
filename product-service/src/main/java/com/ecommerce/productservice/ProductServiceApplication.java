package com.ecommerce.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 產品服務應用程式
 * 
 * 此應用程式提供產品相關的功能，包括產品管理、分類和搜尋等。
 * 它是電商平台的核心服務之一，負責管理產品資料。
 * 使用 MySQL 儲存產品的基本資訊，使用 MongoDB 儲存產品的詳細描述和圖片資訊。
 */
@SpringBootApplication  // 標記為 Spring Boot 應用程式
public class ProductServiceApplication {
    
    /**
     * 應用程式主入口點
     * 
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}