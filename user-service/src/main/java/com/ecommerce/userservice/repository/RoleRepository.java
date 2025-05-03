package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色儲存庫介面
 * 
 * 此介面繼承自 JpaRepository，提供對 Role 實體的基本 CRUD 操作。
 * Spring Data JPA 會自動實現此介面，無需手動編寫實現類。
 */
@Repository  // 標記為儲存庫組件
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根據角色名稱查找角色
     * 
     * @param name 角色名稱，使用 ERole 枚舉類型
     * @return 包含角色的 Optional 對象，如果未找到則為空
     */
    Optional<Role> findByName(Role.ERole name);
}