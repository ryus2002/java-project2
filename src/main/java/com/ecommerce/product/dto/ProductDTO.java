package com.ecommerce.product.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 產品數據傳輸對象
 * 用於在服務層和表示層之間傳輸產品數據
 */
public class ProductDTO {
    
    /**
     * 產品ID
     */
    private Long id;
    
    /**
     * 產品名稱
     * 不能為空，最大長度為100個字符
     */
    @NotBlank(message = "產品名稱不能為空")
    @Size(max = 100, message = "產品名稱不能超過100個字符")
    private String name;
    
    /**
     * 產品描述
     * 不能為空，最大長度為500個字符
     */
    @NotBlank(message = "產品描述不能為空")
    @Size(max = 500, message = "產品描述不能超過500個字符")
    private String description;
    
    /**
     * 產品價格
     * 不能為空，必須大於0
     */
    @NotNull(message = "產品價格不能為空")
    @DecimalMin(value = "0.01", message = "產品價格必須大於0")
    private BigDecimal price;
    
    /**
     * 產品庫存數量
     * 不能為空，必須大於等於0
     */
    @NotNull(message = "產品庫存數量不能為空")
    @Min(value = 0, message = "產品庫存數量不能小於0")
    private Integer stockQuantity;
    
    /**
     * 產品所屬類別ID
     */
    private Long categoryId;
    
    /**
     * 產品所屬類別名稱
     */
    private String categoryName;
    
    /**
     * 產品圖片URL
     */
    private String imageUrl;
    
    /**
     * 默認構造函數
     */
    public ProductDTO() {
    }
    
    /**
     * 帶參數的構造函數
     * 
     * @param id 產品ID
     * @param name 產品名稱
     * @param description 產品描述
     * @param price 產品價格
     * @param stockQuantity 產品庫存數量
     * @param categoryId 產品所屬類別ID
     * @param categoryName 產品所屬類別名稱
     * @param imageUrl 產品圖片URL
     */
    public ProductDTO(Long id, String name, String description, BigDecimal price, Integer stockQuantity,
                     Long categoryId, String categoryName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
    }
    
    /**
     * 獲取產品ID
     * 
     * @return 產品ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 設置產品ID
     * 
     * @param id 產品ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 獲取產品名稱
     * 
     * @return 產品名稱
     */
    public String getName() {
        return name;
    }
    
    /**
     * 設置產品名稱
     * 
     * @param name 產品名稱
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 獲取產品描述
     * 
     * @return 產品描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 設置產品描述
     * 
     * @param description 產品描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 獲取產品價格
     * 
     * @return 產品價格
     */
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * 設置產品價格
     * 
     * @param price 產品價格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    /**
     * 獲取產品庫存數量
     * 
     * @return 產品庫存數量
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    /**
     * 設置產品庫存數量
     * 
     * @param stockQuantity 產品庫存數量
     */
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    /**
     * 獲取產品所屬類別ID
     * 
     * @return 產品所屬類別ID
     */
    public Long getCategoryId() {
        return categoryId;
    }
    
    /**
     * 設置產品所屬類別ID
     * 
     * @param categoryId 產品所屬類別ID
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    /**
     * 獲取產品所屬類別名稱
     * 
     * @return 產品所屬類別名稱
     */
    public String getCategoryName() {
        return categoryName;
    }
    
    /**
     * 設置產品所屬類別名稱
     * 
     * @param categoryName 產品所屬類別名稱
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    /**
     * 獲取產品圖片URL
     * 
     * @return 產品圖片URL
     */
    public String getImageUrl() {
        return imageUrl;
    }
    
    /**
     * 設置產品圖片URL
     * 
     * @param imageUrl 產品圖片URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    /**
     * 重寫toString方法
     * 
     * @return 產品DTO的字符串表示
     */
    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}