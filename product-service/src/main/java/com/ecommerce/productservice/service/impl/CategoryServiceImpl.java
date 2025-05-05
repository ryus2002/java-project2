package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.mapper.CategoryMapper;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 類別服務實現類
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 創建類別
     *
     * @param categoryDTO 類別DTO
     * @return 創建後的類別DTO
     */
    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 檢查父類別是否存在
        if (categoryDTO.getParentId() != null) {
            categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));
        }
        
        // 使用映射器將DTO轉換為實體
        Category category = categoryMapper.toEntity(categoryDTO);
        
        // 保存類別到MySQL
        Category savedCategory = categoryRepository.save(category);
        
        // 使用映射器將實體轉換為DTO並返回
        return categoryMapper.toDTO(savedCategory);
    }

    /**
     * 更新類別
     *
     * @param id 類別ID
     * @param categoryDTO 類別DTO
     * @return 更新後的類別DTO
     */
    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // 檢查類別是否存在
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // 檢查父類別是否存在
        if (categoryDTO.getParentId() != null) {
            // 檢查是否將類別設為自己的父類別
            if (id.equals(categoryDTO.getParentId())) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));
        }
        
        // 使用映射器更新類別實體
        categoryMapper.updateEntityFromDTO(category, categoryDTO);
        
        // 保存更新後的類別到MySQL
        Category updatedCategory = categoryRepository.save(category);
        
        // 使用映射器將實體轉換為DTO並返回
        return categoryMapper.toDTO(updatedCategory);
    }

    /**
     * 根據ID獲取類別
     *
     * @param id 類別ID
     * @return 包含類別的Optional對象，如果未找到則為空
     */
    @Override
    public Optional<CategoryDTO> getCategoryById(Long id) {
        // 根據ID查詢類別
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    /**
     * 獲取所有類別
     *
     * @param pageable 分頁參數
     * @return 分頁的類別列表
     */
    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        // 分頁查詢所有類別
        Page<Category> categories = categoryRepository.findAll(pageable);
        
        // 使用映射器將每個類別實體轉換為DTO
        return categories.map(categoryMapper::toDTO);
    }

    /**
     * 刪除類別
     *
     * @param id 類別ID
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 檢查類別是否存在
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // 檢查類別是否有子類別
        List<Category> subcategories = categoryRepository.findByParentId(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }
        
        // 刪除類別
        categoryRepository.delete(category);
    }

    /**
     * 獲取頂級類別（沒有父類別的類別）
     *
     * @return 頂級類別列表
     */
    @Override
    public List<CategoryDTO> getTopLevelCategories() {
        // 查詢所有沒有父類別的類別
        List<Category> topLevelCategories = categoryRepository.findByParentIdIsNull();
        
        // 使用映射器將類別實體列表轉換為DTO列表
        return categoryMapper.toDTOList(topLevelCategories);
    }

    /**
     * 根據父類別ID獲取子類別
     *
     * @param parentId 父類別ID
     * @return 子類別列表
     */
    @Override
    public List<CategoryDTO> getSubcategories(Long parentId) {
        // 檢查父類別是否存在
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + parentId));
        
        // 根據父類別ID查詢子類別
        List<Category> subcategories = categoryRepository.findByParentId(parentId);
        
        // 使用映射器將類別實體列表轉換為DTO列表
        return categoryMapper.toDTOList(subcategories);
    }

    /**
     * 獲取類別樹
     *
     * @return 類別樹結構
     */
    @Override
    public List<Map<String, Object>> getCategoryTree() {
        // 查詢類別樹
        List<Object[]> categoryRows = categoryRepository.findCategoryTree();
        
        // 使用映射器構建類別樹結構
        return categoryMapper.buildCategoryTree(categoryRows);
    }

    /**
     * 根據名稱搜尋類別
     *
     * @param name 類別名稱關鍵字
     * @return 符合條件的類別列表
     */
    @Override
    public List<CategoryDTO> searchCategoriesByName(String name) {
        // 根據名稱搜尋類別
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
        
        // 使用映射器將類別實體列表轉換為DTO列表
        return categoryMapper.toDTOList(categories);
    }
}