package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 類別服務接口
 */
public interface CategoryService {
    
    /**
     * 創建類別
     *
     * @param categoryDTO 類別DTO
     * @return 創建後的類別DTO
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    /**
     * 更新類別
     *
     * @param id 類別ID
     * @param categoryDTO 類別DTO
     * @return 更新後的類別DTO
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    
    /**
     * 根據ID獲取類別
     *
     * @param id 類別ID
     * @return 包含類別的Optional對象，如果未找到則為空
     */
    Optional<CategoryDTO> getCategoryById(Long id);
    
    /**
     * 獲取所有類別
     *
     * @param pageable 分頁參數
     * @return 分頁的類別列表
     */
    Page<CategoryDTO> getAllCategories(Pageable pageable);
    
    /**
     * 刪除類別
     *
     * @param id 類別ID
     */
    void deleteCategory(Long id);
    
    /**
     * 獲取頂級類別（沒有父類別的類別）
     *
     * @return 頂級類別列表
     */
    List<CategoryDTO> getTopLevelCategories();
    
    /**
     * 根據父類別ID獲取子類別
     *
     * @param parentId 父類別ID
     * @return 子類別列表
     */
    List<CategoryDTO> getSubcategories(Long parentId);
    
    /**
     * 獲取類別樹
     *
     * @return 類別樹結構
     */
    List<Map<String, Object>> getCategoryTree();
    
    /**
     * 根據名稱搜尋類別
     *
     * @param name 類別名稱關鍵字
     * @return 符合條件的類別列表
     */
    List<CategoryDTO> searchCategoriesByName(String name);
}