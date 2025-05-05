package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 類別資料庫訪問接口
 * 提供類別實體的CRUD操作
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根據類別名稱查詢類別
     * @param name 類別名稱
     * @return 類別對象
     */
    Category findByName(String name);
    
    /**
     * 檢查指定名稱的類別是否存在
     * @param name 類別名稱
     * @return 如果存在返回true，否則返回false
     */
    boolean existsByName(String name);
}