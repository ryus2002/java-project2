package com.ecommerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 產品類別數據傳輸對象
 * 
 * 此類用於封裝產品類別的信息，包括其子類別。
 * 用於在控制器和客戶端之間傳輸數據，避免直接暴露實體類。
 * 支持樹狀結構的表示，便於前端顯示類別層次結構。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Boolean isActive;
    
    // 子類別列表，用於構建樹狀結構
    private List<CategoryDTO> children = new ArrayList<>();
}