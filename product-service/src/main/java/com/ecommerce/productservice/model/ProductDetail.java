package com.ecommerce.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 產品詳細信息實體類
 * 
 * 此類映射到 MongoDB 資料庫中的 product_details 集合，用於存儲產品的詳細信息。
 * 包括產品的詳細描述、規格、圖片 URL 等不適合存儲在關聯資料庫中的資訊。
 */
@Document(collection = "product_details")  // 指定 MongoDB 集合名稱
@Data                                      // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@NoArgsConstructor                         // Lombok 註解，自動生成無參構造函數
@AllArgsConstructor                        // Lombok 註解，自動生成全參構造函數
public class ProductDetail {
    
    @Id  // MongoDB 文檔 ID
    private String id;
    
    private Long productId;  // 關聯到 MySQL 中的產品 ID
    
    private String fullDescription;  // 產品詳細描述，可以是 HTML 格式
    
    private List<String> imageUrls = new ArrayList<>();  // 產品圖片 URL 列表
    
    private Map<String, String> specifications;  // 產品規格，如尺寸、顏色、材質等
    
    private List<String> features = new ArrayList<>();  // 產品特點列表
    
    private List<String> tags = new ArrayList<>();  // 產品標籤，用於搜尋和分類
    
    private Map<String, Object> additionalInfo;  // 額外的產品信息，使用 Map 存儲靈活的資料結構
}