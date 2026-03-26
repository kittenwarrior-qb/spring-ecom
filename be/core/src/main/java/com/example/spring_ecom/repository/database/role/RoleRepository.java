package com.example.spring_ecom.repository.database.role;

import com.example.spring_ecom.repository.database.role.dao.RolePermissionDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
    
    Optional<String> findNameById(Long id);
    
    /**
     * Get all role-permission mappings in a single query
     * Returns flat list that can be grouped in Java
     */
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.role.dao.RolePermissionDao(
            r.id, r.name, p.name
        )
        FROM RoleEntity r
        LEFT JOIN RolePermissionEntity rp ON r.id = rp.roleId
        LEFT JOIN PermissionEntity p ON rp.permissionId = p.id
        ORDER BY r.id
        """)
    List<RolePermissionDao> findAllRolePermissions();
}
