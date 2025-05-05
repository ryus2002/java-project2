package com.ecommerce.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置類
 * 
 * 用於配置 Swagger UI 和 OpenAPI 文檔的基本信息
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置 OpenAPI 基本信息
     * 
     * @param serverUrl 服務器URL（從配置文件中獲取）
     * @return OpenAPI 配置對象
     */
    @Bean
    public OpenAPI customOpenAPI(@Value("${server.servlet.context-path:}") String contextPath) {
        // 創建服務器列表
        Server localServer = new Server()
                .url("http://localhost:8082" + contextPath)
                .description("本地開發環境");
        
        Server productionServer = new Server()
                .url("https://api.ecommerce.com" + contextPath)
                .description("生產環境");
        
        // 創建聯絡人信息
        Contact contact = new Contact()
                .name("開發團隊")
                .email("dev@ecommerce.com")
                .url("https://ecommerce.com/support");
        
        // 創建許可證信息
        License license = new License()
                .name("私有許可")
                .url("https://ecommerce.com/license");
        
        // 創建API信息
        Info info = new Info()
                .title("產品服務 API")
                .version("1.0.0")
                .description("產品服務的RESTful API文檔，包含產品和類別的管理功能")
                .contact(contact)
                .license(license);
        
        // 返回OpenAPI配置
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}