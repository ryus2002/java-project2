package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.dto.CategoryDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 類別映射器
 * 
 * 負責在類別實體和類別DTO之間進行轉換
 */
@Component
public class CategoryMapper {
    
    /**
     * 將類別實體轉換為類別DTO
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
        dto.setParentId(category.getParentId());
        dto.setIsActive(category.getIsActive());
        
        return dto;
    }
    
    /**
     * 將類別DTO轉換為類別實體
     * 
     * @param dto 類別DTO
     * @return 類別實體
     */
    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setParentId(dto.getParentId());
        category.setIsActive(dto.getIsActive());
        
        return category;
    }
    
    /**
     * 將類別實體列表轉換為類別DTO列表
     * 
     * @param categories 類別實體列表
     * @return 類別DTO列表
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 從資料庫查詢結果構建類別樹
     * 
     * @param categoryRows 類別查詢結果
     * @return 類別樹結構
     */
    public List<Map<String, Object>> buildCategoryTree(List<Object[]> categoryRows) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 實現類別樹的構建邏輯
        // 這裡需要根據實際的查詢結果格式來實現
        
        return result;
    }
    
    /**
     * 使用類別DTO更新類別實體
     * 
     * @param category 現有類別實體
     * @param dto 類別DTO
     */
    public void updateEntityFromDTO(Category category, CategoryDTO dto) {
        if (category == null || dto == null) {
            return;
        }
        
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }
        
        if (dto.getIsActive() != null) {
            category.setIsActive(dto.getIsActive());
        }
    }
}