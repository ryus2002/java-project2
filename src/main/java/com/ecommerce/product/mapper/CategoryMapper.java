package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.dto.CategoryRequest;
import com.ecommerce.product.entity.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 類別映射器
 * 負責類別實體與DTO之間的轉換
 */
@Component
public class CategoryMapper {
    
    /**
     * 將類別實體轉換為DTO
     * 
     * @param category 類別實體
     * @return 類別DTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        
        // 設置父類別信息
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        
        // 設置子類別ID列表
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildrenIds(category.getChildren().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()));
        }
        
        // 設置產品數量
        dto.setProductCount(category.getProducts().size());
        
        return dto;
    }
    
    /**
     * 將類別請求對象轉換為類別實體
     * 注意：此方法不設置類別ID、父類別和子類別，這些需要在服務層中處理
     * 
     * @param request 類別請求對象
     * @return 類別實體
     */
    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        
        return category;
    }
    
    /**
     * 更新類別實體
     * 使用類別請求對象的數據更新現有類別實體
     * 注意：此方法不更新類別ID、父類別和子類別，這些需要在服務層中處理
     * 
     * @param category 現有類別實體
     * @param request 類別請求對象
     */
    public void updateEntityFromRequest(Category category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
    }
}