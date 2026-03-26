package com.example.spring_ecom.repository.database.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);
    
    @Query("SELECT p.name FROM PermissionEntity p " +
           "JOIN RolePermissionEntity rp ON p.id = rp.permissionId " +
           "JOIN UserRoleEntity ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId")
    List<String> findPermissionsByUserId(@Param("userId") Long userId);
}
