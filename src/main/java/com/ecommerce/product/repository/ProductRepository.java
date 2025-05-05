package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 產品資料庫訪問接口
 * 提供產品實體的CRUD操作
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根據類別ID查詢產品
     * @param categoryId 類別ID
     * @return 產品列表
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * 根據產品名稱或描述進行模糊搜索
     * @param name 產品名稱關鍵字
     * @param description 產品描述關鍵字
     * @param pageable 分頁參數
     * @return 符合條件的產品分頁結果
     */
    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}