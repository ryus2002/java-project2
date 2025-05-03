package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 產品服務介面
 * 
 * 定義產品相關的業務邏輯操作，包括產品的 CRUD、搜尋和過濾等功能。
 * 將業務邏輯與控制器分離，提高代碼的可維護性和可測試性。
 */
public interface ProductService {
    
    /**
     * 創建產品
     * 
     * @param productRequest 產品創建請求
     * @return 創建後的產品 DTO
     */
    ProductDTO createProduct(ProductCreateRequest productRequest);
    
    /**
     * 更新產品
     * 
     * @param id 產品 ID
     * @param productRequest 產品更新請求
     * @return 更新後的產品 DTO
     */
    ProductDTO updateProduct(Long id, ProductUpdateRequest productRequest);
    
    /**
     * 根據 ID 獲取產品
     * 
     * @param id 產品 ID
     * @return 包含產品的 Optional 對象，如果未找到則為空
     */
    Optional<ProductDTO> getProductById(Long id);
    
    /**
     * 獲取所有產品
     * 
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> getAllProducts(Pageable pageable);
    
    /**
     * 刪除產品
     * 
     * @param id 產品 ID
     */
    void deleteProduct(Long id);
    
    /**
     * 根據名稱搜尋產品
     * 
     * @param name 產品名稱關鍵字
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> searchProductsByName(String name, Pageable pageable);
    
    /**
     * 根據類別 ID 獲取產品
     * 
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 根據價格範圍獲取產品
     * 
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * 根據品牌獲取產品
     * 
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> getProductsByBrand(String brand, Pageable pageable);
    
    /**
     * 獲取熱門產品
     * 
     * @param limit 返回的產品數量
     * @return 熱門產品列表
     */
    List<ProductDTO> getTopProducts(int limit);
    
    /**
     * 根據多個條件過濾產品
     * 
     * @param name 產品名稱關鍵字
     * @param categoryId 類別 ID
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    Page<ProductDTO> filterProducts(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String brand, Pageable pageable);
    
    /**
     * 更新產品庫存
     * 
     * @param id 產品 ID
     * @param quantity 要增加或減少的數量（正數增加，負數減少）
     * @return 更新後的產品 DTO
     */
    ProductDTO updateProductStock(Long id, Integer quantity);
}