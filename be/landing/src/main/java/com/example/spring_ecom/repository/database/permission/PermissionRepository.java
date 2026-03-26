package com.example.spring_ecom.repository.database.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    
    List<PermissionEntity> findAllByIdIn(List<Long> ids);
    
    @Query("""
        SELECT p.name FROM PermissionEntity p
        JOIN RolePermissionEntity rp ON p.id = rp.permissionId
        WHERE rp.roleId = :roleId
        """)
    List<String> findPermissionsByRoleId(@Param("roleId") Long roleId);
    
    @Query("""
        SELECT DISTINCT p.name FROM PermissionEntity p
        JOIN RolePermissionEntity rp ON p.id = rp.permissionId
        JOIN UserRoleEntity ur ON ur.roleId = rp.roleId
        WHERE ur.userId = :userId
        """)
    List<String> findPermissionsByUserId(@Param("userId") Long userId);
}
