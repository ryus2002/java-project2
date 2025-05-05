package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductCreateRequest;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.dto.ProductUpdateRequest;
import com.ecommerce.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 產品控制器
 * 
 * 處理與產品相關的所有HTTP請求，包括產品的CRUD操作、搜尋和過濾等功能。
 * 提供RESTful API，允許客戶端與產品服務進行交互。
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "產品管理", description = "產品管理相關的API，包括產品的CRUD操作、搜尋和過濾等功能")
public class ProductController {
    
    // 注入產品服務
    @Autowired
    private ProductService productService;
    
    /**
     * 創建產品
     * 
     * @param productRequest 產品創建請求
     * @return 創建後的產品DTO和HTTP狀態碼
     */
    @PostMapping
    @Operation(summary = "創建產品", description = "創建一個新產品，包括基本信息和詳細信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "產品創建成功", 
                     content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問")
    })
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductCreateRequest productRequest) {
        // 調用服務層創建產品
        ProductDTO createdProduct = productService.createProduct(productRequest);
        // 返回創建成功的狀態碼和產品數據
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    
    /**
     * 更新產品
     * 
     * @param id 產品ID
     * @param productRequest 產品更新請求
     * @return 更新後的產品DTO和HTTP狀態碼
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新產品", description = "根據ID更新產品信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "產品更新成功", 
                     content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問"),
        @ApiResponse(responseCode = "404", description = "產品不存在")
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "產品ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody ProductUpdateRequest productRequest) {
        // 調用服務層更新產品
        ProductDTO updatedProduct = productService.updateProduct(id, productRequest);
        // 返回更新後的產品數據
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 根據ID獲取產品
     * 
     * @param id 產品ID
     * @return 產品DTO和HTTP狀態碼
     */
    @GetMapping("/{id}")
    @Operation(summary = "獲取產品", description = "根據ID獲取產品詳細信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品", 
                     content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "產品不存在")
    })
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "產品ID", required = true) @PathVariable Long id) {
        // 調用服務層獲取產品
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 獲取所有產品（分頁）
     * 
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @param sort 排序欄位
     * @param direction 排序方向（asc或desc）
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping
    @Operation(summary = "獲取所有產品", description = "分頁獲取所有產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序欄位") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "排序方向（asc或desc）") @RequestParam(defaultValue = "asc") String direction) {
        // 創建分頁和排序參數
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
        // 調用服務層獲取產品列表
        Page<ProductDTO> products = productService.getAllProducts(pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 刪除產品
     * 
     * @param id 產品ID
     * @return HTTP狀態碼
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除產品", description = "根據ID刪除產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "產品刪除成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問"),
        @ApiResponse(responseCode = "404", description = "產品不存在")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "產品ID", required = true) @PathVariable Long id) {
        // 調用服務層刪除產品
        productService.deleteProduct(id);
        // 返回刪除成功的狀態碼
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根據名稱搜尋產品
     * 
     * @param name 產品名稱關鍵字
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping("/search")
    @Operation(summary = "搜尋產品", description = "根據名稱關鍵字搜尋產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductDTO>> searchProductsByName(
            @Parameter(description = "產品名稱關鍵字", required = true) @RequestParam String name,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        // 創建分頁參數
        Pageable pageable = PageRequest.of(page, size);
        // 調用服務層搜尋產品
        Page<ProductDTO> products = productService.searchProductsByName(name, pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根據類別獲取產品
     * 
     * @param categoryId 類別ID
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根據類別獲取產品", description = "獲取指定類別的所有產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "類別不存在")
    })
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @Parameter(description = "類別ID", required = true) @PathVariable Long categoryId,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        // 創建分頁參數
        Pageable pageable = PageRequest.of(page, size);
        // 調用服務層獲取產品列表
        Page<ProductDTO> products = productService.getProductsByCategory(categoryId, pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根據價格範圍獲取產品
     * 
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping("/price-range")
    @Operation(summary = "根據價格範圍獲取產品", description = "獲取指定價格範圍內的所有產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductDTO>> getProductsByPriceRange(
            @Parameter(description = "最低價格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高價格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        // 如果最低價格為空，設置為0
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        // 如果最高價格為空，設置為最大值
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999999");
        }
        // 創建分頁參數
        Pageable pageable = PageRequest.of(page, size);
        // 調用服務層獲取產品列表
        Page<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根據品牌獲取產品
     * 
     * @param brand 品牌名稱
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping("/brand/{brand}")
    @Operation(summary = "根據品牌獲取產品", description = "獲取指定品牌的所有產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductDTO>> getProductsByBrand(
            @Parameter(description = "品牌名稱", required = true) @PathVariable String brand,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        // 創建分頁參數
        Pageable pageable = PageRequest.of(page, size);
        // 調用服務層獲取產品列表
        Page<ProductDTO> products = productService.getProductsByBrand(brand, pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 獲取熱門產品
     * 
     * @param limit 返回的產品數量
     * @return 熱門產品列表和HTTP狀態碼
     */
    @GetMapping("/top")
    @Operation(summary = "獲取熱門產品", description = "獲取指定數量的熱門產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取熱門產品列表", 
                     content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ProductDTO>> getTopProducts(
            @Parameter(description = "返回的產品數量") @RequestParam(defaultValue = "10") int limit) {
        // 調用服務層獲取熱門產品
        List<ProductDTO> topProducts = productService.getTopProducts(limit);
        // 返回產品列表
        return ResponseEntity.ok(topProducts);
    }
    
    /**
     * 根據多個條件過濾產品
     * 
     * @param name 產品名稱關鍵字
     * @param categoryId 類別ID
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param brand 品牌名稱
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @return 分頁的產品列表和HTTP狀態碼
     */
    @GetMapping("/filter")
    @Operation(summary = "過濾產品", description = "根據多個條件過濾產品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取產品列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductDTO>> filterProducts(
            @Parameter(description = "產品名稱關鍵字") @RequestParam(required = false) String name,
            @Parameter(description = "類別ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "最低價格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高價格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "品牌名稱") @RequestParam(required = false) String brand,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        // 創建分頁參數
        Pageable pageable = PageRequest.of(page, size);
        // 調用服務層過濾產品
        Page<ProductDTO> products = productService.filterProducts(name, categoryId, minPrice, maxPrice, brand, pageable);
        // 返回產品列表
        return ResponseEntity.ok(products);
    }
    
    /**
     * 更新產品庫存
     * 
     * @param id 產品ID
     * @param quantity 要增加或減少的數量（正數增加，負數減少）
     * @return 更新後的產品DTO和HTTP狀態碼
     */
    @PatchMapping("/{id}/stock")
    @Operation(summary = "更新產品庫存", description = "增加或減少產品庫存")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "庫存更新成功", 
                     content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "404", description = "產品不存在")
    })
    public ResponseEntity<ProductDTO> updateProductStock(
            @Parameter(description = "產品ID", required = true) @PathVariable Long id,
            @Parameter(description = "要增加或減少的數量（正數增加，負數減少）", required = true) @RequestParam Integer quantity) {
        // 調用服務層更新產品庫存
        ProductDTO updatedProduct = productService.updateProductStock(id, quantity);
        // 返回更新後的產品數據
        return ResponseEntity.ok(updatedProduct);
    }
}