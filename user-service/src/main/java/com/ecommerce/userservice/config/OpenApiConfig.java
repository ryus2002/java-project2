package com.ecommerce.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 配置類
 * 
 * 此類配置 Swagger 文檔的基本信息和安全方案。
 * 使用 SpringDoc OpenAPI 實現，提供 API 的自動文檔化。
 * 
 * @author YourName
 * @version 1.0
 * @since 2023-05-03
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * 配置 OpenAPI 基本信息
     * 
     * @return OpenAPI 配置對象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 定義 API 標題、描述、版本等基本信息
        return new OpenAPI()
                .info(new Info()
                        .title("用戶服務 API")
                        .description("智慧電商微服務平台的用戶服務 API 文檔")
                        .version("1.0")
                        .contact(new Contact()
                                .name("開發團隊")
                                .email("team@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // 添加 JWT 安全方案
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .name("JWT")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("請輸入 JWT Token")));
    }
}