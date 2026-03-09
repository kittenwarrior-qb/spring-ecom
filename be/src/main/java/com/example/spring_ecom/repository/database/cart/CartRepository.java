package com.example.spring_ecom.repository.database.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    
    Optional<CartEntity> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
