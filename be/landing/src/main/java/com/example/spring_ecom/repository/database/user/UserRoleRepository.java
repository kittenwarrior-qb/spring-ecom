package com.example.spring_ecom.repository.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    
    List<UserRoleEntity> findByUserId(Long userId);
    
    @Query("SELECT ur.roleId FROM UserRoleEntity ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
    
    void deleteByUserId(Long userId);
}
