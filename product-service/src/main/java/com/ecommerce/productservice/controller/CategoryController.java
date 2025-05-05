package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.service.CategoryService;
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

import java.util.List;
import java.util.Map;

/**
 * 產品類別控制器
 * 
 * 處理與產品類別相關的所有HTTP請求，包括類別的CRUD操作。
 * 提供RESTful API，允許客戶端與產品類別服務進行交互。
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "產品類別管理", description = "產品類別管理相關的API，包括類別的CRUD操作")
public class CategoryController {
    
    // 注入類別服務
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 創建類別
     * 
     * @param categoryDTO 類別DTO
     * @return 創建後的類別DTO和HTTP狀態碼
     */
    @PostMapping
    @Operation(summary = "創建類別", description = "創建一個新的產品類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "類別創建成功", 
                     content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問")
    })
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        // 調用服務層創建類別
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        // 返回創建成功的狀態碼和類別數據
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }
    
    /**
     * 更新類別
     * 
     * @param id 類別ID
     * @param categoryDTO 類別DTO
     * @return 更新後的類別DTO和HTTP狀態碼
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新類別", description = "根據ID更新產品類別信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "類別更新成功", 
                     content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問"),
        @ApiResponse(responseCode = "404", description = "類別不存在")
    })
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "類別ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody CategoryDTO categoryDTO) {
        // 調用服務層更新類別
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        // 返回更新後的類別數據
        return ResponseEntity.ok(updatedCategory);
    }
    
    /**
     * 根據ID獲取類別
     * 
     * @param id 類別ID
     * @return 類別DTO和HTTP狀態碼
     */
    @GetMapping("/{id}")
    @Operation(summary = "獲取類別", description = "根據ID獲取產品類別信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取類別", 
                     content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(responseCode = "404", description = "類別不存在")
    })
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "類別ID", required = true) @PathVariable Long id) {
        // 調用服務層獲取類別
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 獲取所有類別（分頁）
     * 
     * @param page 頁碼（從0開始）
     * @param size 每頁大小
     * @param sort 排序欄位
     * @param direction 排序方向（asc或desc）
     * @return 分頁的類別列表和HTTP狀態碼
     */
    @GetMapping
    @Operation(summary = "獲取所有類別", description = "分頁獲取所有產品類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取類別列表", 
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序欄位") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "排序方向（asc或desc）") @RequestParam(defaultValue = "asc") String direction) {
        // 創建分頁和排序參數
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
        // 調用服務層獲取類別列表
        Page<CategoryDTO> categories = categoryService.getAllCategories(pageable);
        // 返回類別列表
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 刪除類別
     * 
     * @param id 類別ID
     * @return HTTP狀態碼
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除類別", description = "根據ID刪除產品類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "類別刪除成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "禁止訪問"),
        @ApiResponse(responseCode = "404", description = "類別不存在")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "類別ID", required = true) @PathVariable Long id) {
        // 調用服務層刪除類別
        categoryService.deleteCategory(id);
        // 返回刪除成功的狀態碼
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 獲取頂級類別（沒有父類別的類別）
     * 
     * @return 頂級類別列表和HTTP狀態碼
     */
    @GetMapping("/top-level")
    @Operation(summary = "獲取頂級類別", description = "獲取所有沒有父類別的頂級類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取頂級類別列表", 
                     content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CategoryDTO>> getTopLevelCategories() {
        // 調用服務層獲取頂級類別
        List<CategoryDTO> topLevelCategories = categoryService.getTopLevelCategories();
        // 返回頂級類別列表
        return ResponseEntity.ok(topLevelCategories);
}
    
    /**
     * 根據父類別ID獲取子類別
     * 
     * @param parentId 父類別ID
     * @return 子類別列表和HTTP狀態碼
     */
    @GetMapping("/subcategories/{parentId}")
    @Operation(summary = "獲取子類別", description = "根據父類別ID獲取所有子類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取子類別列表", 
                     content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "404", description = "父類別不存在")
    })
    public ResponseEntity<List<CategoryDTO>> getSubcategories(
            @Parameter(description = "父類別ID", required = true) @PathVariable Long parentId) {
        // 調用服務層獲取子類別
        List<CategoryDTO> subcategories = categoryService.getSubcategories(parentId);
        // 返回子類別列表
        return ResponseEntity.ok(subcategories);
    }
    
    /**
     * 獲取類別樹
     * 
     * @return 類別樹結構和HTTP狀態碼
     */
    @GetMapping("/tree")
    @Operation(summary = "獲取類別樹", description = "獲取完整的類別樹結構")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取類別樹", 
                     content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<Map<String, Object>>> getCategoryTree() {
        // 調用服務層獲取類別樹
        List<Map<String, Object>> categoryTree = categoryService.getCategoryTree();
        // 返回類別樹
        return ResponseEntity.ok(categoryTree);
    }
    
    /**
     * 根據名稱搜尋類別
     * 
     * @param name 類別名稱關鍵字
     * @return 符合條件的類別列表和HTTP狀態碼
     */
    @GetMapping("/search")
    @Operation(summary = "搜尋類別", description = "根據名稱關鍵字搜尋類別")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取類別列表", 
                     content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CategoryDTO>> searchCategoriesByName(
            @Parameter(description = "類別名稱關鍵字", required = true) @RequestParam String name) {
        // 調用服務層搜尋類別
        List<CategoryDTO> categories = categoryService.searchCategoriesByName(name);
        // 返回類別列表
        return ResponseEntity.ok(categories);
    }
}