package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 產品儲存庫介面
 * 
 * 此介面繼承自 JpaRepository，提供對 Product 實體的基本 CRUD 操作。
 * 同時定義了一些自定義的查詢方法，用於實現產品的搜尋和過濾功能。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根據產品名稱模糊查詢
     * 
     * @param name 產品名稱關鍵字
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * 根據類別 ID 查詢產品
     * 
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * 根據價格範圍查詢產品
     * 
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * 根據品牌查詢產品
     * 
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<Product> findByBrand(String brand, Pageable pageable);
    
    /**
     * 查詢庫存不足的產品
     * 
     * @param threshold 庫存閾值
     * @return 庫存不足的產品列表
     */
    List<Product> findByStockLessThan(Integer threshold);
    
    /**
     * 查詢熱門產品
     * 
     * 使用自定義 SQL 查詢，根據訂單量排序產品
     * 此方法需要訂單服務的支持，可能需要通過消息佇列或事件驅動來更新產品的熱門程度
     * 
     * @param limit 返回的產品數量
     * @return 熱門產品列表
     */
    @Query(value = "SELECT p.* FROM products p WHERE p.is_active = true ORDER BY p.popularity DESC LIMIT ?1", nativeQuery = true)
    List<Product> findTopProducts(int limit);
    
    /**
     * 根據多個條件查詢產品
     * 
     * @param name 產品名稱關鍵字
     * @param categoryId 類別 ID
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:brand IS NULL OR p.brand = :brand) AND " +
           "p.isActive = true")
    Page<Product> findProductsByFilters(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String brand, Pageable pageable);
}