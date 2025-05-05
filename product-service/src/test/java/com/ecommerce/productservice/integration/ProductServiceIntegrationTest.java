package com.ecommerce.productservice.integration;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 產品服務整合測試
 * 
 * 測試產品服務的端到端功能，包括資料庫操作和API響應
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long categoryId;
    private ProductCreateRequest productCreateRequest;
    private ProductUpdateRequest productUpdateRequest;

    /**
     * 測試前準備工作
     */
    @BeforeEach
    void setUp() {
        // 創建測試類別
        Category testCategory = new Category();
        testCategory.setName("整合測試類別");
        testCategory.setDescription("用於整合測試的類別");
        testCategory.setParentId(null);
        testCategory.setIsActive(true);
        
        Category savedCategory = categoryRepository.save(testCategory);
        categoryId = savedCategory.getId();

        // 初始化產品創建請求
        productCreateRequest = new ProductCreateRequest();
        productCreateRequest.setName("整合測試產品");
        productCreateRequest.setShortDescription("這是一個用於整合測試的產品");
        productCreateRequest.setPrice(new BigDecimal("99.99"));
        productCreateRequest.setStock(100);
        productCreateRequest.setCategoryId(categoryId);
        productCreateRequest.setSku("INT-001");
        productCreateRequest.setBrand("測試品牌");
        productCreateRequest.setWeight(new BigDecimal("0.5"));
        productCreateRequest.setDimensions("10 x 10 x 5 cm");
        productCreateRequest.setFullDescription("這是一個用於整合測試的產品的詳細描述");

        // 初始化產品更新請求
        productUpdateRequest = new ProductUpdateRequest();
        productUpdateRequest.setName("更新的整合測試產品");
        productUpdateRequest.setShortDescription("這是一個更新後的整合測試產品");
        productUpdateRequest.setPrice(new BigDecimal("199.99"));
        productUpdateRequest.setStock(200);
        productUpdateRequest.setCategoryId(categoryId);
        productUpdateRequest.setSku("INT-002");
        productUpdateRequest.setBrand("更新品牌");
        productUpdateRequest.setWeight(new BigDecimal("1.0"));
        productUpdateRequest.setDimensions("20 x 15 x 10 cm");
        productUpdateRequest.setFullDescription("這是一個更新後的整合測試產品的詳細描述");
        productUpdateRequest.setIsActive(true);
    }

    /**
     * 測試產品的完整生命週期：創建、獲取、更新、刪除
     */
    @Test
    @DisplayName("測試產品的完整生命週期")
    void testProductLifecycle() throws Exception {
        // 1. 創建產品
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("整合測試產品")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andReturn();

        // 從創建結果中獲取產品ID
        String createResultContent = createResult.getResponse().getContentAsString();
        ProductDTO createdProduct = objectMapper.readValue(createResultContent, ProductDTO.class);
        Long productId = createdProduct.getId();
        assertNotNull(productId);

        // 2. 獲取產品
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("整合測試產品")))
                .andExpect(jsonPath("$.price", is(99.99)));

        // 3. 更新產品
        mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("更新的整合測試產品")))
                .andExpect(jsonPath("$.price", is(199.99)));

        // 4. 再次獲取產品，驗證更新是否成功
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("更新的整合測試產品")))
                .andExpect(jsonPath("$.price", is(199.99)));

        // 5. 更新產品庫存
        mockMvc.perform(patch("/api/products/" + productId + "/stock")
                .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.stock", is(250))); // 原有200 + 新增50

        // 6. 刪除產品
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());

        // 7. 嘗試獲取已刪除的產品，應該返回404
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isNotFound());
    }

    /**
     * 測試根據類別獲取產品
     */
    @Test
    @DisplayName("測試根據類別獲取產品")
    void testGetProductsByCategory() throws Exception {
        // 1. 創建兩個產品，同一類別
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
                .andExpect(status().isCreated());

        ProductCreateRequest anotherProduct = new ProductCreateRequest();
        anotherProduct.setName("另一個測試產品");
        anotherProduct.setShortDescription("這是另一個測試產品");
        anotherProduct.setPrice(new BigDecimal("199.99"));
        anotherProduct.setStock(50);
        anotherProduct.setCategoryId(categoryId);
        anotherProduct.setSku("INT-003");
        anotherProduct.setBrand("測試品牌");
        anotherProduct.setWeight(new BigDecimal("1.0"));
        anotherProduct.setDimensions("15 x 15 x 10 cm");
        anotherProduct.setFullDescription("這是另一個測試產品的詳細描述");

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(anotherProduct)))
                .andExpect(status().isCreated());

        // 2. 根據類別獲取產品
        mockMvc.perform(get("/api/products/category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].categoryId", is(categoryId.intValue())))
                .andExpect(jsonPath("$.content[1].categoryId", is(categoryId.intValue())));
    }

    /**
     * 測試根據價格範圍獲取產品
     */
    @Test
    @DisplayName("測試根據價格範圍獲取產品")
    void testGetProductsByPriceRange() throws Exception {
        // 1. 創建三個不同價格的產品
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest))) // 價格99.99
                .andExpect(status().isCreated());

        ProductCreateRequest product2 = new ProductCreateRequest();
        product2.setName("中價產品");
        product2.setShortDescription("這是一個中價產品");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setStock(50);
        product2.setCategoryId(categoryId);
        product2.setSku("INT-004");
        product2.setBrand("測試品牌");
        product2.setWeight(new BigDecimal("1.0"));
        product2.setDimensions("15 x 15 x 10 cm");
        product2.setFullDescription("這是一個中價產品的詳細描述");

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product2))) // 價格199.99
                .andExpect(status().isCreated());

        ProductCreateRequest product3 = new ProductCreateRequest();
        product3.setName("高價產品");
        product3.setShortDescription("這是一個高價產品");
        product3.setPrice(new BigDecimal("299.99"));
        product3.setStock(30);
        product3.setCategoryId(categoryId);
        product3.setSku("INT-005");
        product3.setBrand("測試品牌");
        product3.setWeight(new BigDecimal("1.5"));
        product3.setDimensions("20 x 20 x 15 cm");
        product3.setFullDescription("這是一個高價產品的詳細描述");

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product3))) // 價格299.99
                .andExpect(status().isCreated());

        // 2. 根據價格範圍獲取產品 (100-250)
        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "100")
                .param("maxPrice", "250"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].price", is(199.99)));

        // 3. 根據價格範圍獲取產品 (0-300)
        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "0")
                .param("maxPrice", "300"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)));
    }
}