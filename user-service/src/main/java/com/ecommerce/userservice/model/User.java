package com.ecommerce.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用戶實體類
 * 
 * 此類映射到資料庫中的 users 表，用於存儲用戶資訊。
 * 包含用戶的基本資訊、認證資訊和角色關聯。
 */
@Entity
@Table(name = "users")  // 指定表名為 users
@Data                   // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@NoArgsConstructor      // Lombok 註解，自動生成無參構造函數
@AllArgsConstructor     // Lombok 註解，自動生成全參構造函數
public class User {
    
    @Id  // 主鍵標記
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主鍵生成策略為自增
    private Long id;
    
    @Column(nullable = false, unique = true)  // 非空且唯一
    private String username;
    
    @Column(nullable = false, unique = true)  // 非空且唯一
    private String email;
    
    @Column(nullable = false)  // 非空
    private String password;
    
    @Column(name = "first_name")  // 指定列名
    private String firstName;
    
    @Column(name = "last_name")   // 指定列名
    private String lastName;
    
    private String phone;
    
    private boolean enabled = true;  // 默認啟用
    
    @Column(name = "created_at")  // 指定列名
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")  // 指定列名
    private LocalDateTime updatedAt;
    
    /**
     * 用戶與角色的多對多關聯
     * 
     * 一個用戶可以有多個角色，一個角色可以被多個用戶擁有。
     * 使用中間表 user_roles 存儲關聯關係。
     */
    @ManyToMany(fetch = FetchType.EAGER)  // 立即加載角色資訊
    @JoinTable(
        name = "user_roles",              // 中間表名稱
        joinColumns = @JoinColumn(name = "user_id"),            // 當前實體在中間表的外鍵
        inverseJoinColumns = @JoinColumn(name = "role_id")      // 關聯實體在中間表的外鍵
    )
    private Set<Role> roles = new HashSet<>();
    
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