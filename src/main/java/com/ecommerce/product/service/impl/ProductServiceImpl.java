package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 產品服務實現類
 * 負責產品相關業務邏輯的實現
 */
@Service
public class ProductServiceImpl implements ProductService {

    // 注入產品資料庫訪問接口
    @Autowired
    private ProductRepository productRepository;
    
    // 注入類別資料庫訪問接口
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 注入產品映射器
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 根據ID查詢產品
     * @param id 產品ID
     * @return 產品DTO對象
     * @throws ResourceNotFoundException 如果產品不存在則拋出異常
     */
    @Override
    public ProductDTO getProductById(Long id) {
        // 從資料庫查詢產品，如果不存在則拋出異常
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // 使用映射器將實體對象轉換為DTO對象並返回
        return productMapper.toDTO(product);
    }
    
    /**
     * 分頁查詢所有產品
     * @param pageable 分頁參數
     * @return 產品DTO對象的分頁結果
     */
    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        // 分頁查詢所有產品
        Page<Product> products = productRepository.findAll(pageable);
        
        // 使用映射器將每個產品實體轉換為DTO，並保持分頁結構
        return products.map(productMapper::toDTO);
    }
    
    /**
     * 根據類別ID查詢產品
     * @param categoryId 類別ID
     * @return 產品DTO對象列表
     */
    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        // 檢查類別是否存在
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        // 根據類別ID查詢產品
        List<Product> products = productRepository.findByCategoryId(categoryId);
        
        // 使用映射器將產品實體列表轉換為DTO列表
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        }
        
    /**
     * 創建新產品
     * @param productRequest 產品請求對象
     * @return 創建後的產品DTO對象
     * @throws ResourceNotFoundException 如果關聯的類別不存在則拋出異常
     */
    @Override
    @Transactional
    public ProductDTO createProduct(ProductRequest productRequest) {
        // 檢查類別是否存在
        Category category = null;
        if (productRequest.getCategoryId() != null) {
            category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
}
        
        // 使用映射器創建新的產品實體
        Product product = productMapper.toEntity(productRequest);
        
        // 設置產品類別
        product.setCategory(category);
        
        // 保存產品到資料庫
        Product savedProduct = productRepository.save(product);
        
        // 使用映射器將保存後的實體轉換為DTO並返回
        return productMapper.toDTO(savedProduct);
    }
    
    /**
     * 更新產品信息
     * @param id 產品ID
     * @param productRequest 產品請求對象
     * @return 更新後的產品DTO對象
     * @throws ResourceNotFoundException 如果產品或關聯的類別不存在則拋出異常
     */
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequest productRequest) {
        // 檢查產品是否存在
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // 檢查類別是否存在
        Category category = null;
        if (productRequest.getCategoryId() != null) {
            category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
        }
        
        // 使用映射器更新產品信息
        productMapper.updateEntityFromRequest(product, productRequest);
        
        // 設置產品類別
        product.setCategory(category);
        
        // 保存更新後的產品到資料庫
        Product updatedProduct = productRepository.save(product);
        
        // 使用映射器將更新後的實體轉換為DTO並返回
        return productMapper.toDTO(updatedProduct);
    }
    
    /**
     * 刪除產品
     * @param id 產品ID
     * @throws ResourceNotFoundException 如果產品不存在則拋出異常
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 檢查產品是否存在
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        // 刪除產品
        productRepository.deleteById(id);
    }
    
    /**
     * 更新產品庫存
     * @param id 產品ID
     * @param quantity 更新的數量（可以是正數或負數）
     * @return 更新後的產品DTO對象
     * @throws ResourceNotFoundException 如果產品不存在則拋出異常
     * @throws IllegalArgumentException 如果更新後庫存為負數則拋出異常
     */
    @Override
    @Transactional
    public ProductDTO updateProductStock(Long id, Integer quantity) {
        // 檢查產品是否存在
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // 計算新的庫存數量
        int newStockQuantity = product.getStockQuantity() + quantity;
        
        // 檢查新庫存是否為負數
        if (newStockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        // 更新庫存數量
        product.setStockQuantity(newStockQuantity);
        
        // 保存更新後的產品到資料庫
        Product updatedProduct = productRepository.save(product);
        
        // 使用映射器將更新後的實體轉換為DTO並返回
        return productMapper.toDTO(updatedProduct);
    }
    
    /**
     * 搜索產品
     * @param keyword 搜索關鍵字
     * @param pageable 分頁參數
     * @return 符合搜索條件的產品DTO對象的分頁結果
     */
    @Override
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        // 根據關鍵字搜索產品（名稱或描述中包含關鍵字）
        Page<Product> products = productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        
        // 使用映射器將產品實體分頁結果轉換為DTO分頁結果
        return products.map(productMapper::toDTO);
    }
}