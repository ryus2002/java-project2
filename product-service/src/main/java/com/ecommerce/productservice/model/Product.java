package com.ecommerce.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 產品實體類
 * 
 * 此類映射到 MySQL 資料庫中的 products 表，用於存儲產品的基本信息。
 * 產品的詳細描述和圖片資訊存儲在 MongoDB 中的 ProductDetail 集合中。
 */
@Entity
@Table(name = "products")  // 指定表名為 products
@Data                      // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@NoArgsConstructor         // Lombok 註解，自動生成無參構造函數
@AllArgsConstructor        // Lombok 註解，自動生成全參構造函數
public class Product {
    
    @Id  // 主鍵標記
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主鍵生成策略為自增
    private Long id;
    
    @Column(nullable = false)  // 非空
    private String name;
    
    @Column(length = 500)  // 指定列長度
    private String shortDescription;
    
    @Column(nullable = false)  // 非空
    private BigDecimal price;
    
    private Integer stock;
    
    @Column(name = "category_id")  // 指定列名
    private Long categoryId;
    
    private String sku;  // Stock Keeping Unit，庫存單位
    
    private String brand;
    
    private BigDecimal weight;
    
    private String dimensions;
    
    @Column(name = "is_active")  // 指定列名
    private Boolean isActive = true;
    
    @Column(name = "created_at")  // 指定列名
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")  // 指定列名
    private LocalDateTime updatedAt;
    
    /**
     * 實體創建前的回調方法
     * 
     * 在實體被持久化之前自動設置創建時間和更新時間。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 實體更新前的回調方法
     * 
     * 在實體被更新之前自動更新更新時間。
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}