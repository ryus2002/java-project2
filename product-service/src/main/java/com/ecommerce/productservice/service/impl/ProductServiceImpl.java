package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.mapper.ProductMapper;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductDetail;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductDetailRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 產品服務實現類
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 創建產品
     *
     * @param productRequest 產品創建請求
     * @return 創建後的產品DTO
     */
    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateRequest productRequest) {
        // 檢查類別是否存在
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        // 使用映射器將請求轉換為產品實體
        Product product = productMapper.toProductEntity(productRequest);
        
        // 保存產品到MySQL
        Product savedProduct = productRepository.save(product);
        
        // 使用映射器將請求轉換為產品詳情實體
        ProductDetail productDetail = productMapper.toProductDetailEntity(productRequest, savedProduct.getId());
        
        // 保存產品詳情到MongoDB
        ProductDetail savedProductDetail = productDetailRepository.save(productDetail);
        
        // 使用映射器將實體轉換為DTO並返回
        return productMapper.toDTO(savedProduct, savedProductDetail, category.getName());
    }

    /**
     * 更新產品
     *
     * @param id 產品ID
     * @param productRequest 產品更新請求
     * @return 更新後的產品DTO
     */
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateRequest productRequest) {
        // 檢查產品是否存在
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // 如果更新了類別，檢查新類別是否存在
        String categoryName = null;
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            categoryName = category.getName();
        } else {
            // 獲取現有類別名稱
            Category category = categoryRepository.findById(product.getCategoryId())
                    .orElse(null);
            if (category != null) {
                categoryName = category.getName();
            }
        }
        
        // 使用映射器更新產品實體
        productMapper.updateProductFromRequest(product, productRequest);
        
        // 保存更新後的產品到MySQL
        Product updatedProduct = productRepository.save(product);
        
        // 獲取產品詳情
        ProductDetail productDetail = productDetailRepository.findByProductId(id)
                .orElse(new ProductDetail());
        
        if (productDetail.getProductId() == null) {
            productDetail.setProductId(id);
        }
        
        // 使用映射器更新產品詳情實體
        productMapper.updateProductDetailFromRequest(productDetail, productRequest);
        
        // 保存更新後的產品詳情到MongoDB
        ProductDetail updatedProductDetail = productDetailRepository.save(productDetail);
        
        // 使用映射器將實體轉換為DTO並返回
        return productMapper.toDTO(updatedProduct, updatedProductDetail, categoryName);
    }

    /**
     * 根據ID獲取產品
     *
     * @param id 產品ID
     * @return 包含產品的Optional對象，如果未找到則為空
     */
    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        // 從MySQL獲取產品
        Optional<Product> productOptional = productRepository.findById(id);
        
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            
            // 從MongoDB獲取產品詳情
            Optional<ProductDetail> productDetailOptional = productDetailRepository.findByProductId(id);
            ProductDetail productDetail = productDetailOptional.orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            ProductDTO productDTO = productMapper.toDTO(product, productDetail, categoryName);
            return Optional.of(productDTO);
        }
        
        return Optional.empty();
    }

    /**
     * 獲取所有產品
     *
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        // 分頁查詢所有產品
        Page<Product> products = productRepository.findAll(pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, categoryName);
        });
    }

    /**
     * 刪除產品
     *
     * @param id 產品ID
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 檢查產品是否存在
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        // 刪除MongoDB中的產品詳情
        productDetailRepository.deleteByProductId(id);
        
        // 刪除MySQL中的產品
        productRepository.deleteById(id);
    }

    /**
     * 根據名稱搜尋產品
     *
     * @param name 產品名稱關鍵字
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> searchProductsByName(String name, Pageable pageable) {
        // 根據名稱搜尋產品
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, categoryName);
        });
    }

    /**
     * 根據類別ID獲取產品
     *
     * @param categoryId 類別ID
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        // 檢查類別是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        // 根據類別ID查詢產品
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, category.getName());
        });
    }

    /**
     * 根據價格範圍獲取產品
     *
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        // 根據價格範圍查詢產品
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, categoryName);
        });
    }

    /**
     * 根據品牌獲取產品
     *
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> getProductsByBrand(String brand, Pageable pageable) {
        // 根據品牌查詢產品
        Page<Product> products = productRepository.findByBrand(brand, pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, categoryName);
        });
    }

    /**
     * 獲取熱門產品
     *
     * @param limit 返回的產品數量
     * @return 熱門產品列表
     */
    @Override
    public List<ProductDTO> getTopProducts(int limit) {
        // 獲取熱門產品
        List<Product> topProducts = productRepository.findTopProducts(limit);
        
        // 將實體轉換為DTO（使用Lambda表達式）
        return topProducts.stream()
                .map(product -> {
                    // 從MongoDB獲取產品詳情
                    ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
                    
                    // 獲取類別名稱
                    String categoryName = null;
                    if (product.getCategoryId() != null) {
                        Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                        if (categoryOptional.isPresent()) {
                            categoryName = categoryOptional.get().getName();
                        }
                    }
                    
                    // 使用映射器將實體轉換為DTO
                    return productMapper.toDTO(product, productDetail, categoryName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 根據多個條件過濾產品
     *
     * @param name 產品名稱關鍵字
     * @param categoryId 類別ID
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param brand 品牌名稱
     * @param pageable 分頁參數
     * @return 分頁的產品列表
     */
    @Override
    public Page<ProductDTO> filterProducts(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String brand, Pageable pageable) {
        // 根據多個條件查詢產品
        Page<Product> products = productRepository.findProductsByFilters(name, categoryId, minPrice, maxPrice, brand, pageable);
        
        // 使用映射器將每個產品實體轉換為DTO
        return products.map(product -> {
            // 從MongoDB獲取產品詳情
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElse(null);
            
            // 獲取類別名稱
            String categoryName = null;
            if (product.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
                if (categoryOptional.isPresent()) {
                    categoryName = categoryOptional.get().getName();
                }
            }
            
            // 使用映射器將實體轉換為DTO
            return productMapper.toDTO(product, productDetail, categoryName);
        });
    }

    /**
     * 更新產品庫存
     *
     * @param id 產品ID
     * @param quantity 要增加或減少的數量（正數增加，負數減少）
     * @return 更新後的產品DTO
     */
    @Override
    @Transactional
    public ProductDTO updateProductStock(Long id, Integer quantity) {
        // 檢查產品是否存在
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // 更新庫存
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product with id: " + id);
        }
        
        product.setStock(newStock);
        
        // 保存更新後的產品到MySQL
        Product updatedProduct = productRepository.save(product);
        
        // 從MongoDB獲取產品詳情
        ProductDetail productDetail = productDetailRepository.findByProductId(id).orElse(null);
        
        // 獲取類別名稱
        String categoryName = null;
        if (product.getCategoryId() != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
            if (categoryOptional.isPresent()) {
                categoryName = categoryOptional.get().getName();
            }
        }
        
        // 使用映射器將實體轉換為DTO並返回
        return productMapper.toDTO(updatedProduct, productDetail, categoryName);
    }
}