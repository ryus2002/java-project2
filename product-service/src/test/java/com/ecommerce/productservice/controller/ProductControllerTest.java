package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 產品控制器單元測試
 * 
 * 測試產品控制器的各個端點，確保HTTP請求處理正確
 */
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO testProductDTO;
    private ProductCreateRequest testCreateRequest;
    private ProductUpdateRequest testUpdateRequest;

    /**
     * 測試前準備工作
     */
    @BeforeEach
    void setUp() {
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
    void testGetProductById_Success() throws Exception {
        // 設置模擬行為
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProductDTO));

        // 執行測試
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試產品")))
                .andExpect(jsonPath("$.price", is(99.99)));

        // 驗證方法調用
        verify(productService).getProductById(1L);
    }

    /**
     * 測試根據ID獲取產品 - 產品不存在
     */
    @Test
    @DisplayName("測試根據ID獲取產品 - 產品不存在")
    void testGetProductById_NotFound() throws Exception {
        // 設置模擬行為
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        // 執行測試
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(productService).getProductById(99L);
    }

    /**
     * 測試獲取所有產品（分頁）
     */
    @Test
    @DisplayName("測試獲取所有產品（分頁）")
    void testGetAllProducts() throws Exception {
        // 準備測試數據
        List<ProductDTO> products = new ArrayList<>();
        products.add(testProductDTO);
        Page<ProductDTO> productPage = new PageImpl<>(products);

        // 設置模擬行為
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        // 執行測試
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("測試產品")));

        // 驗證方法調用
        verify(productService).getAllProducts(any(Pageable.class));
    }

    /**
     * 測試創建產品
     */
    @Test
    @DisplayName("測試創建產品")
    void testCreateProduct() throws Exception {
        // 設置模擬行為
        when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(testProductDTO);

        // 執行測試
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試產品")));

        // 驗證方法調用
        verify(productService).createProduct(any(ProductCreateRequest.class));
    }

    /**
     * 測試更新產品
     */
    @Test
    @DisplayName("測試更新產品")
    void testUpdateProduct() throws Exception {
        // 設置模擬行為
        when(productService.updateProduct(eq(1L), any(ProductUpdateRequest.class))).thenReturn(testProductDTO);

        // 執行測試
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試產品")));

        // 驗證方法調用
        verify(productService).updateProduct(eq(1L), any(ProductUpdateRequest.class));
    }

    /**
     * 測試更新產品 - 產品不存在
     */
    @Test
    @DisplayName("測試更新產品 - 產品不存在")
    void testUpdateProduct_NotFound() throws Exception {
        // 設置模擬行為
        when(productService.updateProduct(eq(99L), any(ProductUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("產品不存在"));

        // 執行測試
        mockMvc.perform(put("/api/products/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(productService).updateProduct(eq(99L), any(ProductUpdateRequest.class));
    }

    /**
     * 測試刪除產品
     */
    @Test
    @DisplayName("測試刪除產品")
    void testDeleteProduct() throws Exception {
        // 設置模擬行為
        doNothing().when(productService).deleteProduct(1L);

        // 執行測試
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        // 驗證方法調用
        verify(productService).deleteProduct(1L);
    }

    /**
     * 測試刪除產品 - 產品不存在
     */
    @Test
    @DisplayName("測試刪除產品 - 產品不存在")
    void testDeleteProduct_NotFound() throws Exception {
        // 設置模擬行為
        doThrow(new ResourceNotFoundException("產品不存在")).when(productService).deleteProduct(99L);

        // 執行測試
        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(productService).deleteProduct(99L);
    }

    /**
     * 測試更新產品庫存
     */
    @Test
    @DisplayName("測試更新產品庫存")
    void testUpdateProductStock() throws Exception {
        // 設置模擬行為
        when(productService.updateProductStock(eq(1L), anyInt())).thenReturn(testProductDTO);

        // 執行測試
        mockMvc.perform(patch("/api/products/1/stock")
                .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試產品")));

        // 驗證方法調用
        verify(productService).updateProductStock(eq(1L), eq(50));
    }

    /**
     * 測試更新產品庫存 - 產品不存在
     */
    @Test
    @DisplayName("測試更新產品庫存 - 產品不存在")
    void testUpdateProductStock_NotFound() throws Exception {
        // 設置模擬行為
        when(productService.updateProductStock(eq(99L), anyInt()))
                .thenThrow(new ResourceNotFoundException("產品不存在"));

        // 執行測試
        mockMvc.perform(patch("/api/products/99/stock")
                .param("quantity", "50"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(productService).updateProductStock(eq(99L), eq(50));
    }
}