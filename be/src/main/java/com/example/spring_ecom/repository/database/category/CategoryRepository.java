package com.example.spring_ecom.repository.database.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    Optional<CategoryEntity> findBySlugAndDeletedAtIsNull(String slug);
    
    List<CategoryEntity> findByDeletedAtIsNullOrderByDisplayOrderAsc();
    
    List<CategoryEntity> findByParentIdAndDeletedAtIsNullOrderByDisplayOrderAsc(Long parentId);
    
    List<CategoryEntity> findByIsActiveAndDeletedAtIsNullOrderByDisplayOrderAsc(Boolean isActive);
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
