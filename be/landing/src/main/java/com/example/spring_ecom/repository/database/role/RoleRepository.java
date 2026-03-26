package com.example.spring_ecom.repository.database.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByName(String name);
    
    List<RoleEntity> findAllByIdIn(List<Long> ids);
    
    @Query("SELECT r.name FROM RoleEntity r WHERE r.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
    
    @Query(value = """
        SELECT r.name FROM roles r
        JOIN user_roles ur ON r.id = ur.role_id
        WHERE ur.user_id = :userId
        """, nativeQuery = true)
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}
