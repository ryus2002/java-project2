package com.ecommerce.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 產品更新請求 DTO
 * 
 * 此類用於封裝更新產品時客戶端提交的數據。
 * 與創建請求不同，更新請求中的大多數字段都是可選的，
 * 只更新提供的字段，未提供的字段保持不變。
 */
@Data
public class ProductUpdateRequest {
    
    // 基本信息 (將更新到 MySQL 的 Product 表)
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String name;
    
    @Size(max = 500, message = "Short description must be less than 500 characters")
    private String shortDescription;
    
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    
    private Long categoryId;
    private String sku;
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isActive;
    
    // 詳細信息 (將更新到 MongoDB 的 ProductDetail 集合)
    private String fullDescription;
    private List<String> imageUrls = new ArrayList<>();
    private Map<String, String> specifications;
    private List<String> features = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> additionalInfo;
}