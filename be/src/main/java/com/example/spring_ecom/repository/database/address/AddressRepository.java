package com.example.spring_ecom.repository.database.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    
    @Query("SELECT a FROM AddressEntity a WHERE a.userId = :userId AND a.deletedAt IS NULL ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<AddressEntity> findByUserIdOrderByIsDefaultDescCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT a FROM AddressEntity a WHERE a.id = :id AND a.userId = :userId AND a.deletedAt IS NULL")
    Optional<AddressEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Query("SELECT a FROM AddressEntity a WHERE a.userId = :userId AND a.isDefault = true AND a.deletedAt IS NULL")
    Optional<AddressEntity> findByUserIdAndIsDefaultTrue(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.id != :addressId AND a.deletedAt IS NULL")
    void unsetDefaultForUser(@Param("userId") Long userId, @Param("addressId") Long addressId);
    
    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.deletedAt IS NULL")
    void unsetAllDefaultForUser(@Param("userId") Long userId);
}
