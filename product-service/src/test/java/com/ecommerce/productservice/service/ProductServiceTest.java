package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductDetail;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductDetailRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 產品服務層單元測試
 * 
 * 測試產品服務層的各個方法，確保業務邏輯正確運行
 */
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductDetailRepository productDetailRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product testProduct;
    private ProductDetail testProductDetail;
    private ProductDTO testProductDTO;
    private ProductCreateRequest testCreateRequest;
    private ProductUpdateRequest testUpdateRequest;
    
    /**
     * 測試前準備工作
     */
    @BeforeEach
    void setUp() {
        // 初始化測試產品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("測試產品");
        testProduct.setShortDescription("這是一個測試產品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setCategoryId(1L);
        testProduct.setSku("TST-001");
        testProduct.setBrand("測試品牌");
        testProduct.setWeight(new BigDecimal("0.5"));
        testProduct.setDimensions("10 x 10 x 5 cm");
        testProduct.setIsActive(true);
        
        // 初始化測試產品詳情
        testProductDetail = new ProductDetail();
        testProductDetail.setId("1");
        testProductDetail.setProductId(1L);
        testProductDetail.setFullDescription("這是一個測試產品的詳細描述");
        testProductDetail.setImageUrls(new ArrayList<>());
        testProductDetail.setFeatures(new ArrayList<>());
        testProductDetail.setTags(new ArrayList<>());
        
        // 初始化測試產品DTO
        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("測試產品");
        testProductDTO.setShortDescription("這是一個測試產品");
        testProductDTO.setPrice(new BigDecimal("99.99"));
        testProductDTO.setStock(100);
        testProductDTO.setCategoryId(1L);
        testProductDTO.setSku("TST-001");
        testProductDTO.setBrand("測試品牌");
        
        // 初始化測試產品創建請求
        testCreateRequest = new ProductCreateRequest();
        testCreateRequest.setName("新產品");
        testCreateRequest.setShortDescription("這是一個新產品");
        testCreateRequest.setPrice(new BigDecimal("199.99"));
        testCreateRequest.setStock(50);
        testCreateRequest.setCategoryId(1L);
        testCreateRequest.setSku("NEW-001");
        testCreateRequest.setBrand("新品牌");
        testCreateRequest.setWeight(new BigDecimal("1.0"));
        testCreateRequest.setDimensions("20 x 15 x 10 cm");
        
        // 初始化測試產品更新請求
        testUpdateRequest = new ProductUpdateRequest();
        testUpdateRequest.setName("更新產品");
        testUpdateRequest.setShortDescription("這是一個更新後的產品");
        testUpdateRequest.setPrice(new BigDecimal("299.99"));
        testUpdateRequest.setStock(200);
        testUpdateRequest.setCategoryId(2L);
        testUpdateRequest.setSku("UPD-001");
        testUpdateRequest.setBrand("更新品牌");
        testUpdateRequest.setWeight(new BigDecimal("1.5"));
        testUpdateRequest.setDimensions("25 x 20 x 15 cm");
        testUpdateRequest.setIsActive(true);
    }
    
    /**
     * 測試根據ID獲取產品
     */
    @Test
    @DisplayName("測試根據ID獲取產品 - 成功")
    void testGetProductById_Success() {
        // 設置模擬行為
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));
        
        // 執行測試
        Optional<ProductDTO> result = productService.getProductById(1L);
        
        // 驗證結果
        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        assertEquals(testProduct.getName(), result.get().getName());
        assertEquals(testProduct.getPrice(), result.get().getPrice());
        
        // 驗證方法調用
        verify(productRepository).findById(1L);
        verify(productDetailRepository).findByProductId(1L);
    }
    
    /**
     * 測試根據ID獲取產品 - 產品不存在
     */
    @Test
    @DisplayName("測試根據ID獲取產品 - 產品不存在")
    void testGetProductById_NotFound() {
        // 設置模擬行為
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        
        // 執行測試
        Optional<ProductDTO> result = productService.getProductById(99L);
        
        // 驗證結果
        assertFalse(result.isPresent());
        
        // 驗證方法調用
        verify(productRepository).findById(99L);
        verify(productDetailRepository, never()).findByProductId(anyLong());
    }
    
    /**
     * 測試獲取所有產品（分頁）
     */
    @Test
    @DisplayName("測試獲取所有產品（分頁）")
    void testGetAllProducts() {
        // 準備測試數據
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        
        // 設置模擬行為
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productDetailRepository.findByProductId(anyLong())).thenReturn(Optional.of(testProductDetail));
        
        // 執行測試
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.getAllProducts(pageable);
        
        // 驗證結果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProduct.getId(), result.getContent().get(0).getId());
        assertEquals(testProduct.getName(), result.getContent().get(0).getName());
        
        // 驗證方法調用
        verify(productRepository).findAll(pageable);
        verify(productDetailRepository).findByProductId(testProduct.getId());
    }
    
    /**
     * 測試創建產品
     */
    @Test
    @DisplayName("測試創建產品")
    void testCreateProduct() {
        // 準備測試數據
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName(testCreateRequest.getName());
        newProduct.setShortDescription(testCreateRequest.getShortDescription());
        newProduct.setPrice(testCreateRequest.getPrice());
        newProduct.setStock(testCreateRequest.getStock());
        newProduct.setCategoryId(testCreateRequest.getCategoryId());
        newProduct.setSku(testCreateRequest.getSku());
        newProduct.setBrand(testCreateRequest.getBrand());
        newProduct.setWeight(testCreateRequest.getWeight());
        newProduct.setDimensions(testCreateRequest.getDimensions());
        newProduct.setIsActive(true);
        
        ProductDetail newProductDetail = new ProductDetail();
        newProductDetail.setId("2");
        newProductDetail.setProductId(2L);
        newProductDetail.setFullDescription(testCreateRequest.getFullDescription());
        
        // 設置模擬行為
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);
        when(productDetailRepository.save(any(ProductDetail.class))).thenReturn(newProductDetail);
        
        // 執行測試
        ProductDTO result = productService.createProduct(testCreateRequest);
        
        // 驗證結果
        assertNotNull(result);
        assertEquals(newProduct.getId(), result.getId());
        assertEquals(newProduct.getName(), result.getName());
        assertEquals(newProduct.getPrice(), result.getPrice());
        
        // 驗證方法調用
        verify(categoryRepository).existsById(testCreateRequest.getCategoryId());
        verify(productRepository).save(any(Product.class));
        verify(productDetailRepository).save(any(ProductDetail.class));
    }
    
    /**
     * 測試創建產品 - 類別不存在
     */
    @Test
    @DisplayName("測試創建產品 - 類別不存在")
    void testCreateProduct_CategoryNotFound() {
        // 設置模擬行為
        when(categoryRepository.existsById(anyLong())).thenReturn(false);
        
        // 執行測試並驗證異常
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(testCreateRequest);
        });
        
        // 驗證方法調用
        verify(categoryRepository).existsById(testCreateRequest.getCategoryId());
        verify(productRepository, never()).save(any(Product.class));
        verify(productDetailRepository, never()).save(any(ProductDetail.class));
    }
    
    /**
     * 測試更新產品
     */
    @Test
    @DisplayName("測試更新產品")
    void testUpdateProduct() {
        // 準備測試數據
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName(testUpdateRequest.getName());
        updatedProduct.setShortDescription(testUpdateRequest.getShortDescription());
        updatedProduct.setPrice(testUpdateRequest.getPrice());
        updatedProduct.setStock(testUpdateRequest.getStock());
        updatedProduct.setCategoryId(testUpdateRequest.getCategoryId());
        updatedProduct.setSku(testUpdateRequest.getSku());
        updatedProduct.setBrand(testUpdateRequest.getBrand());
        updatedProduct.setWeight(testUpdateRequest.getWeight());
        updatedProduct.setDimensions(testUpdateRequest.getDimensions());
        updatedProduct.setIsActive(testUpdateRequest.getIsActive());
        
        ProductDetail updatedProductDetail = new ProductDetail();
        updatedProductDetail.setId("1");
        updatedProductDetail.setProductId(1L);
        updatedProductDetail.setFullDescription(testUpdateRequest.getFullDescription());
        
        // 設置模擬行為
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productDetailRepository.save(any(ProductDetail.class))).thenReturn(updatedProductDetail);
        
        // 執行測試
        ProductDTO result = productService.updateProduct(1L, testUpdateRequest);
        
        // 驗證結果
        assertNotNull(result);
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getName(), result.getName());
        assertEquals(updatedProduct.getPrice(), result.getPrice());
        
        // 驗證方法調用
        verify(productRepository).findById(1L);
        verify(productDetailRepository).findByProductId(1L);
        verify(categoryRepository).existsById(testUpdateRequest.getCategoryId());
        verify(productRepository).save(any(Product.class));
        verify(productDetailRepository).save(any(ProductDetail.class));
    }
    
    /**
     * 測試更新產品 - 產品不存在
     */
    @Test
    @DisplayName("測試更新產品 - 產品不存在")
    void testUpdateProduct_ProductNotFound() {
        // 設置模擬行為
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        
        // 執行測試並驗證異常
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(99L, testUpdateRequest);
        });
        
        // 驗證方法調用
        verify(productRepository).findById(99L);
        verify(productDetailRepository, never()).findByProductId(anyLong());
        verify(categoryRepository, never()).existsById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
        verify(productDetailRepository, never()).save(any(ProductDetail.class));
    }
    
    /**
     * 測試刪除產品
     */
    @Test
    @DisplayName("測試刪除產品")
    void testDeleteProduct() {
        // 設置模擬行為
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);
        doNothing().when(productDetailRepository).deleteByProductId(1L);
        
        // 執行測試
        productService.deleteProduct(1L);
        
        // 驗證方法調用
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
        verify(productDetailRepository).deleteByProductId(1L);
    }
    
    /**
     * 測試刪除產品 - 產品不存在
     */
    @Test
    @DisplayName("測試刪除產品 - 產品不存在")
    void testDeleteProduct_ProductNotFound() {
        // 設置模擬行為
        when(productRepository.existsById(99L)).thenReturn(false);
        
        // 執行測試並驗證異常
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(99L);
        });
        
        // 驗證方法調用
        verify(productRepository).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
        verify(productDetailRepository, never()).deleteByProductId(anyLong());
    }
    
    /**
     * 測試更新產品庫存
     */
    @Test
    @DisplayName("測試更新產品庫存")
    void testUpdateProductStock() {
        // 準備測試數據
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName(testProduct.getName());
        updatedProduct.setShortDescription(testProduct.getShortDescription());
        updatedProduct.setPrice(testProduct.getPrice());
        updatedProduct.setStock(150); // 增加50庫存
        updatedProduct.setCategoryId(testProduct.getCategoryId());
        updatedProduct.setSku(testProduct.getSku());
        updatedProduct.setBrand(testProduct.getBrand());
        updatedProduct.setWeight(testProduct.getWeight());
        updatedProduct.setDimensions(testProduct.getDimensions());
        updatedProduct.setIsActive(testProduct.getIsActive());
        
        // 設置模擬行為
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productDetailRepository.findByProductId(1L)).thenReturn(Optional.of(testProductDetail));
        
        // 執行測試
        ProductDTO result = productService.updateProductStock(1L, 50);
        
        // 驗證結果
        assertNotNull(result);
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getStock(), result.getStock());
        
        // 驗證方法調用
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(productDetailRepository).findByProductId(1L);
    }
    
    /**
     * 測試更新產品庫存 - 產品不存在
     */
    @Test
    @DisplayName("測試更新產品庫存 - 產品不存在")
    void testUpdateProductStock_ProductNotFound() {
        // 設置模擬行為
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        
        // 執行測試並驗證異常
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductStock(99L, 50);
        });
        
        // 驗證方法調用
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    /**
     * 測試更新產品庫存 - 庫存不足
     */
    @Test
    @DisplayName("測試更新產品庫存 - 庫存不足")
    void testUpdateProductStock_InsufficientStock() {
        // 設置模擬行為
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // 執行測試並驗證異常
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductStock(1L, -150); // 嘗試減少150庫存，但只有100庫存
        });
        
        // 驗證方法調用
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
}