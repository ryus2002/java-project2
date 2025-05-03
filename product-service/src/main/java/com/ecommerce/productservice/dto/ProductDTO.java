package com.ecommerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 產品數據傳輸對象
 * 
 * 此類用於封裝產品的完整信息，包括基本信息和詳細信息。
 * 用於在控制器和客戶端之間傳輸數據，避免直接暴露實體類。
 * 整合了 MySQL 中的 Product 和 MongoDB 中的 ProductDetail 的數據。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    // 基本信息 (來自 MySQL 的 Product 表)
    private Long id;
    private String name;
    private String shortDescription;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String categoryName;  // 類別名稱，需要從 Category 表中獲取
    private String sku;
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isActive;
    
    // 詳細信息 (來自 MongoDB 的 ProductDetail 集合)
    private String fullDescription;
    private List<String> imageUrls = new ArrayList<>();
    private Map<String, String> specifications;
    private List<String> features = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> additionalInfo;
}