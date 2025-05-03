package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 產品類別儲存庫介面
 * 
 * 此介面繼承自 JpaRepository，提供對 Category 實體的基本 CRUD 操作。
 * 同時定義了一些自定義的查詢方法，用於實現類別的層次結構查詢。
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 查詢頂級類別（沒有父類別的類別）
     * 
     * @return 頂級類別列表
     */
    List<Category> findByParentIdIsNull();
    
    /**
     * 根據父類別 ID 查詢子類別
     * 
     * @param parentId 父類別 ID
     * @return 子類別列表
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * 查詢類別樹
     * 
     * 使用遞歸查詢獲取完整的類別樹結構
     * 
     * @return 類別樹結構的 JSON 字符串
     */
    @Query(value = "WITH RECURSIVE category_tree AS (" +
                   "  SELECT id, name, description, parent_id, 0 AS level" +
                   "  FROM categories" +
                   "  WHERE parent_id IS NULL" +
                   "  UNION ALL" +
                   "  SELECT c.id, c.name, c.description, c.parent_id, ct.level + 1" +
                   "  FROM categories c" +
                   "  JOIN category_tree ct ON c.parent_id = ct.id" +
                   ")" +
                   "SELECT * FROM category_tree ORDER BY level, name", nativeQuery = true)
    List<Object[]> findCategoryTree();
    
    /**
     * 查詢指定類別的所有子類別（包括子類別的子類別）
     * 
     * @param categoryId 類別 ID
     * @return 子類別列表
     */
    @Query(value = "WITH RECURSIVE category_children AS (" +
                   "  SELECT id, name, description, parent_id" +
                   "  FROM categories" +
                   "  WHERE id = ?1" +
                   "  UNION ALL" +
                   "  SELECT c.id, c.name, c.description, c.parent_id" +
                   "  FROM categories c" +
                   "  JOIN category_children cc ON c.parent_id = cc.id" +
                   ")" +
                   "SELECT * FROM category_children WHERE id != ?1", nativeQuery = true)
    List<Object[]> findAllChildCategories(Long categoryId);
    
    /**
     * 根據名稱查詢類別
     * 
     * @param name 類別名稱
     * @return 類別列表
     */
    List<Category> findByNameContainingIgnoreCase(String name);
}