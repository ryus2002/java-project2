package com.ecommerce.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色實體類
 * 
 * 此類映射到資料庫中的 roles 表，用於存儲角色資訊。
 * 角色用於實現基於角色的訪問控制 (RBAC)，定義用戶的權限範圍。
 */
@Entity
@Table(name = "roles")  // 指定表名為 roles
@Data                   // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@NoArgsConstructor      // Lombok 註解，自動生成無參構造函數
@AllArgsConstructor     // Lombok 註解，自動生成全參構造函數
public class Role {
    
    @Id  // 主鍵標記
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主鍵生成策略為自增
    private Long id;
    
    /**
     * 角色名稱，使用枚舉類型
     * 
     * 使用 EnumType.STRING 將枚舉值以字符串形式存儲在資料庫中，提高可讀性
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)  // 長度為20，唯一且非空
    private ERole name;
    
    /**
     * 角色枚舉
     * 
     * 定義系統中的角色類型，包括普通用戶、管理員和版主
     */
    public enum ERole {
        ROLE_USER,       // 普通用戶角色
        ROLE_ADMIN,      // 管理員角色
        ROLE_MODERATOR   // 版主角色
    }
}