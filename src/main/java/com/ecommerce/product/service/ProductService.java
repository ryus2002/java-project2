package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 產品服務接口
 * 定義產品相關的業務邏輯操作
 */
public interface ProductService {
    
    /**
     * 根據ID查詢產品
     * @param id 產品ID
     * @return 產品DTO對象
     */
    ProductDTO getProductById(Long id);
    
    /**
     * 分頁查詢所有產品
     * @param pageable 分頁參數
     * @return 產品DTO對象的分頁結果
     */
    Page<ProductDTO> getAllProducts(Pageable pageable);
    
    /**
     * 根據類別ID查詢產品
     * @param categoryId 類別ID
     * @return 產品DTO對象列表
     */
    List<ProductDTO> getProductsByCategory(Long categoryId);
    
    /**
     * 創建新產品
     * @param productRequest 產品請求對象
     * @return 創建後的產品DTO對象
     */
    ProductDTO createProduct(ProductRequest productRequest);
    
    /**
     * 更新產品信息
     * @param id 產品ID
     * @param productRequest 產品請求對象
     * @return 更新後的產品DTO對象
     */
    ProductDTO updateProduct(Long id, ProductRequest productRequest);
    
    /**
     * 刪除產品
     * @param id 產品ID
     */
    void deleteProduct(Long id);
    
    /**
     * 更新產品庫存
     * @param id 產品ID
     * @param quantity 更新的數量（可以是正數或負數）
     * @return 更新後的產品DTO對象
     */
    ProductDTO updateProductStock(Long id, Integer quantity);
    
    /**
     * 搜索產品
     * @param keyword 搜索關鍵字
     * @param pageable 分頁參數
     * @return 符合搜索條件的產品DTO對象的分頁結果
     */
    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);
}