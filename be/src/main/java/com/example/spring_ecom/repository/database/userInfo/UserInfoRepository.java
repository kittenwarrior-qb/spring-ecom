package com.example.spring_ecom.repository.database.userInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
    
    @Query("SELECT ui FROM UserInfoEntity ui WHERE ui.userId = :userId AND ui.deletedAt IS NULL")
    Optional<UserInfoEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ui FROM UserInfoEntity ui WHERE ui.phoneNumber = :phoneNumber AND ui.deletedAt IS NULL")
    Optional<UserInfoEntity> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByPhoneNumber(String phoneNumber);
}