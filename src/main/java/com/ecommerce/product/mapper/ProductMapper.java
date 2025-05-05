package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.Category;
import org.springframework.stereotype.Component;

/**
 * 產品映射器
 * 負責產品實體與DTO之間的轉換
 */
@Component
public class ProductMapper {
    
    /**
     * 將產品實體轉換為DTO
     * 
     * @param product 產品實體
     * @return 產品DTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        
        // 如果產品有關聯的類別，則設置類別ID和名稱
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        return dto;
    }
    
    /**
     * 將產品請求對象轉換為產品實體
     * 注意：此方法不設置產品ID和類別，這些需要在服務層中處理
     * 
     * @param request 產品請求對象
     * @return 產品實體
     */
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        
        return product;
    }
    
    /**
     * 更新產品實體
     * 使用產品請求對象的數據更新現有產品實體
     * 注意：此方法不更新產品ID和類別，這些需要在服務層中處理
     * 
     * @param product 現有產品實體
     * @param request 產品請求對象
     */
    public void updateEntityFromRequest(Product product, ProductRequest request) {
        if (product == null || request == null) {
            return;
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
    }
}