package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.ProductDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 產品詳細信息儲存庫介面
 * 
 * 此介面繼承自 MongoRepository，提供對 ProductDetail 實體的基本 CRUD 操作。
 * 用於訪問 MongoDB 中存儲的產品詳細信息。
 */
@Repository
public interface ProductDetailRepository extends MongoRepository<ProductDetail, String> {
    
    /**
     * 根據產品 ID 查詢產品詳細信息
     * 
     * @param productId MySQL 中的產品 ID
     * @return 包含產品詳細信息的 Optional 對象，如果未找到則為空
     */
    Optional<ProductDetail> findByProductId(Long productId);
    
    /**
     * 根據產品 ID 刪除產品詳細信息
     * 
     * @param productId MySQL 中的產品 ID
     */
    void deleteByProductId(Long productId);
    
    /**
     * 根據標籤查詢產品詳細信息
     * 
     * @param tag 產品標籤
     * @return 包含該標籤的產品詳細信息列表
     */
    Iterable<ProductDetail> findByTagsContaining(String tag);
}