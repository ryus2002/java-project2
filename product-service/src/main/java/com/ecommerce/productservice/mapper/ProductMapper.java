package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductDetail;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import org.springframework.stereotype.Component;

/**
 * 產品映射器
 * 
 * 負責在產品實體、產品詳情實體和產品DTO之間進行轉換
 */
@Component
public class ProductMapper {
    
    /**
     * 將產品實體和產品詳情實體轉換為產品DTO
     * 
     * @param product 產品實體
     * @param productDetail 產品詳情實體
     * @param categoryName 類別名稱
     * @return 產品DTO
     */
    public ProductDTO toDTO(Product product, ProductDetail productDetail, String categoryName) {
        if (product == null) {
            return null;
        }
        
        ProductDTO dto = new ProductDTO();
        
        // 設置基本信息（來自MySQL的Product表）
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setShortDescription(product.getShortDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryId(product.getCategoryId());
        dto.setCategoryName(categoryName);
        dto.setSku(product.getSku());
        dto.setBrand(product.getBrand());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setIsActive(product.getIsActive());
        
        // 設置詳細信息（來自MongoDB的ProductDetail集合）
        if (productDetail != null) {
            dto.setFullDescription(productDetail.getFullDescription());
            dto.setImageUrls(productDetail.getImageUrls());
            dto.setSpecifications(productDetail.getSpecifications());
            dto.setFeatures(productDetail.getFeatures());
            dto.setTags(productDetail.getTags());
            dto.setAdditionalInfo(productDetail.getAdditionalInfo());
        }
        
        return dto;
    }
    
    /**
     * 將產品創建請求轉換為產品實體
     * 
     * @param request 產品創建請求
     * @return 產品實體
     */
    public Product toProductEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setIsActive(request.getIsActive());
        
        return product;
    }
    
    /**
     * 將產品創建請求轉換為產品詳情實體
     * 
     * @param request 產品創建請求
     * @param productId 產品ID
     * @return 產品詳情實體
     */
    public ProductDetail toProductDetailEntity(ProductCreateRequest request, Long productId) {
        if (request == null) {
            return null;
        }
        
        ProductDetail productDetail = new ProductDetail();
        productDetail.setProductId(productId);
        productDetail.setFullDescription(request.getFullDescription());
        productDetail.setImageUrls(request.getImageUrls());
        productDetail.setSpecifications(request.getSpecifications());
        productDetail.setFeatures(request.getFeatures());
        productDetail.setTags(request.getTags());
        productDetail.setAdditionalInfo(request.getAdditionalInfo());
        
        return productDetail;
    }
    
    /**
     * 使用產品更新請求更新產品實體
     * 
     * @param product 現有產品實體
     * @param request 產品更新請求
     */
    public void updateProductFromRequest(Product product, ProductUpdateRequest request) {
        if (product == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        
        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }
        
        if (request.getDimensions() != null) {
            product.setDimensions(request.getDimensions());
        }
        
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
    }
    
    /**
     * 使用產品更新請求更新產品詳情實體
     * 
     * @param productDetail 現有產品詳情實體
     * @param request 產品更新請求
     */
    public void updateProductDetailFromRequest(ProductDetail productDetail, ProductUpdateRequest request) {
        if (productDetail == null || request == null) {
            return;
        }
        
        if (request.getFullDescription() != null) {
            productDetail.setFullDescription(request.getFullDescription());
        }
        
        if (request.getImageUrls() != null) {
            productDetail.setImageUrls(request.getImageUrls());
        }
        
        if (request.getSpecifications() != null) {
            productDetail.setSpecifications(request.getSpecifications());
        }
        
        if (request.getFeatures() != null) {
            productDetail.setFeatures(request.getFeatures());
        }
        
        if (request.getTags() != null) {
            productDetail.setTags(request.getTags());
        }
        
        if (request.getAdditionalInfo() != null) {
            productDetail.setAdditionalInfo(request.getAdditionalInfo());
        }
    }
}