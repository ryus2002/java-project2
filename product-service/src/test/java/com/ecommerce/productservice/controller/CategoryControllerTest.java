package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.service.CategoryService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 類別控制器單元測試
 * 
 * 測試類別控制器的各個端點，確保HTTP請求處理正確
 */
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryDTO testCategoryDTO;

    /**
     * 測試前準備工作
     */
    @BeforeEach
    void setUp() {
        // 初始化測試類別DTO
        testCategoryDTO = new CategoryDTO();
        testCategoryDTO.setId(1L);
        testCategoryDTO.setName("測試類別");
        testCategoryDTO.setDescription("這是一個測試類別");
        testCategoryDTO.setParentId(null);
        testCategoryDTO.setIsActive(true);
    }

    /**
     * 測試根據ID獲取類別
     */
    @Test
    @DisplayName("測試根據ID獲取類別 - 成功")
    void testGetCategoryById_Success() throws Exception {
        // 設置模擬行為
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategoryDTO));

        // 執行測試
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).getCategoryById(1L);
    }

    /**
     * 測試根據ID獲取類別 - 類別不存在
     */
    @Test
    @DisplayName("測試根據ID獲取類別 - 類別不存在")
    void testGetCategoryById_NotFound() throws Exception {
        // 設置模擬行為
        when(categoryService.getCategoryById(99L)).thenReturn(Optional.empty());

        // 執行測試
        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(categoryService).getCategoryById(99L);
    }

    /**
     * 測試獲取所有類別（分頁）
     */
    @Test
    @DisplayName("測試獲取所有類別（分頁）")
    void testGetAllCategories() throws Exception {
        // 準備測試數據
        List<CategoryDTO> categories = new ArrayList<>();
        categories.add(testCategoryDTO);
        Page<CategoryDTO> categoryPage = new PageImpl<>(categories);

        // 設置模擬行為
        when(categoryService.getAllCategories(any(Pageable.class))).thenReturn(categoryPage);

        // 執行測試
        mockMvc.perform(get("/api/categories")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).getAllCategories(any(Pageable.class));
    }

    /**
     * 測試創建類別
     */
    @Test
    @DisplayName("測試創建類別")
    void testCreateCategory() throws Exception {
        // 設置模擬行為
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(testCategoryDTO);

        // 執行測試
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).createCategory(any(CategoryDTO.class));
    }

    /**
     * 測試更新類別
     */
    @Test
    @DisplayName("測試更新類別")
    void testUpdateCategory() throws Exception {
        // 設置模擬行為
        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(testCategoryDTO);

        // 執行測試
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).updateCategory(eq(1L), any(CategoryDTO.class));
    }

    /**
     * 測試更新類別 - 類別不存在
     */
    @Test
    @DisplayName("測試更新類別 - 類別不存在")
    void testUpdateCategory_NotFound() throws Exception {
        // 設置模擬行為
        when(categoryService.updateCategory(eq(99L), any(CategoryDTO.class)))
                .thenThrow(new ResourceNotFoundException("類別不存在"));

        // 執行測試
        mockMvc.perform(put("/api/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDTO)))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(categoryService).updateCategory(eq(99L), any(CategoryDTO.class));
    }

    /**
     * 測試刪除類別
     */
    @Test
    @DisplayName("測試刪除類別")
    void testDeleteCategory() throws Exception {
        // 設置模擬行為
        doNothing().when(categoryService).deleteCategory(1L);

        // 執行測試
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        // 驗證方法調用
        verify(categoryService).deleteCategory(1L);
    }

    /**
     * 測試刪除類別 - 類別不存在
     */
    @Test
    @DisplayName("測試刪除類別 - 類別不存在")
    void testDeleteCategory_NotFound() throws Exception {
        // 設置模擬行為
        doThrow(new ResourceNotFoundException("類別不存在")).when(categoryService).deleteCategory(99L);

        // 執行測試
        mockMvc.perform(delete("/api/categories/99"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(categoryService).deleteCategory(99L);
    }

    /**
     * 測試獲取頂級類別
     */
    @Test
    @DisplayName("測試獲取頂級類別")
    void testGetTopLevelCategories() throws Exception {
        // 準備測試數據
        List<CategoryDTO> topLevelCategories = new ArrayList<>();
        topLevelCategories.add(testCategoryDTO);

        // 設置模擬行為
        when(categoryService.getTopLevelCategories()).thenReturn(topLevelCategories);

        // 執行測試
        mockMvc.perform(get("/api/categories/top-level"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).getTopLevelCategories();
    }

    /**
     * 測試獲取子類別
     */
    @Test
    @DisplayName("測試獲取子類別")
    void testGetSubcategories() throws Exception {
        // 準備測試數據
        CategoryDTO childCategory = new CategoryDTO();
        childCategory.setId(2L);
        childCategory.setName("子類別");
        childCategory.setDescription("這是一個子類別");
        childCategory.setParentId(1L);
        childCategory.setIsActive(true);

        List<CategoryDTO> subcategories = new ArrayList<>();
        subcategories.add(childCategory);

        // 設置模擬行為
        when(categoryService.getSubcategories(1L)).thenReturn(subcategories);

        // 執行測試
        mockMvc.perform(get("/api/categories/subcategories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("子類別")))
                .andExpect(jsonPath("$[0].parentId", is(1)));

        // 驗證方法調用
        verify(categoryService).getSubcategories(1L);
    }

    /**
     * 測試獲取子類別 - 父類別不存在
     */
    @Test
    @DisplayName("測試獲取子類別 - 父類別不存在")
    void testGetSubcategories_ParentNotFound() throws Exception {
        // 設置模擬行為
        when(categoryService.getSubcategories(99L)).thenThrow(new ResourceNotFoundException("父類別不存在"));

        // 執行測試
        mockMvc.perform(get("/api/categories/subcategories/99"))
                .andExpect(status().isNotFound());

        // 驗證方法調用
        verify(categoryService).getSubcategories(99L);
    }

    /**
     * 測試獲取類別樹
     */
    @Test
    @DisplayName("測試獲取類別樹")
    void testGetCategoryTree() throws Exception {
        // 準備測試數據
        List<Map<String, Object>> categoryTree = new ArrayList<>();
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1L);
        category.put("name", "測試類別");
        category.put("children", new ArrayList<>());
        categoryTree.add(category);

        // 設置模擬行為
        when(categoryService.getCategoryTree()).thenReturn(categoryTree);

        // 執行測試
        mockMvc.perform(get("/api/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("測試類別")))
                .andExpect(jsonPath("$[0].children", hasSize(0)));

        // 驗證方法調用
        verify(categoryService).getCategoryTree();
    }

    /**
     * 測試根據名稱搜尋類別
     */
    @Test
    @DisplayName("測試根據名稱搜尋類別")
    void testSearchCategoriesByName() throws Exception {
        // 準備測試數據
        List<CategoryDTO> searchResults = new ArrayList<>();
        searchResults.add(testCategoryDTO);

        // 設置模擬行為
        when(categoryService.searchCategoriesByName("測試")).thenReturn(searchResults);

        // 執行測試
        mockMvc.perform(get("/api/categories/search")
                .param("name", "測試"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("測試類別")));

        // 驗證方法調用
        verify(categoryService).searchCategoriesByName("測試");
    }
}