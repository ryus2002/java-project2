package com.ecommerce.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 產品創建請求 DTO
 * 
 * 此類用於封裝創建產品時客戶端提交的數據。
 * 包含數據驗證註解，確保提交的數據符合業務規則。
 */
@Data
public class ProductCreateRequest {
    
    // 基本信息 (將存儲到 MySQL 的 Product 表)
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String name;
    
    @Size(max = 500, message = "Short description must be less than 500 characters")
    private String shortDescription;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private String sku;
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isActive = true;
    
    // 詳細信息 (將存儲到 MongoDB 的 ProductDetail 集合)
    private String fullDescription;
    private List<String> imageUrls = new ArrayList<>();
    private Map<String, String> specifications;
    private List<String> features = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> additionalInfo;
}