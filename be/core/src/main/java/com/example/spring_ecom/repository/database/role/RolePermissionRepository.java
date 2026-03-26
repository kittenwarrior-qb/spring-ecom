package com.example.spring_ecom.repository.database.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, RolePermissionId> {

    @Query("SELECT rp.permissionId FROM RolePermissionEntity rp WHERE rp.roleId = :roleId")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
