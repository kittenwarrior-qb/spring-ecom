package com.example.spring_ecom.repository.database.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    Optional<CategoryEntity> findBySlugAndDeletedAtIsNull(String slug);
    
    // Unified method for filtering categories
    @Query("""
        SELECT c FROM CategoryEntity c 
        WHERE c.deletedAt IS NULL
        AND (:parentId IS NULL OR c.parentId = :parentId)
        AND (:isActive IS NULL OR c.isActive = :isActive)
        ORDER BY c.displayOrder ASC
    """)
    List<CategoryEntity> findCategoriesWithFilters(
        @Param("parentId") Long parentId,
        @Param("isActive") Boolean isActive
    );
    
    // Convenience methods for backward compatibility
    default List<CategoryEntity> findByDeletedAtIsNullOrderByDisplayOrderAsc() {
        return findCategoriesWithFilters(null, null);
    }
    
    default List<CategoryEntity> findByParentIdAndDeletedAtIsNullOrderByDisplayOrderAsc(Long parentId) {
        return findCategoriesWithFilters(parentId, null);
    }
    
    default List<CategoryEntity> findByIsActiveAndDeletedAtIsNullOrderByDisplayOrderAsc(Boolean isActive) {
        return findCategoriesWithFilters(null, isActive);
    }
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
