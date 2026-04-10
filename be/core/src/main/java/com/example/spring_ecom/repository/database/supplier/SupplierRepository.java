package com.example.spring_ecom.repository.database.supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {

    @Query("""
        SELECT s FROM SupplierEntity s
        WHERE s.deletedAt IS NULL
        AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(COALESCE(s.contactName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:isActive IS NULL OR s.isActive = :isActive)
        ORDER BY s.createdAt DESC
    """)
    Page<SupplierEntity> searchSuppliersWithKeyword(
        @Param("keyword") String keyword,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );

    Page<SupplierEntity> findByDeletedAtIsNull(Pageable pageable);

    Page<SupplierEntity> findByDeletedAtIsNullAndIsActive(Boolean isActive, Pageable pageable);

    default Page<SupplierEntity> findAllActive(Pageable pageable) {
        return findByDeletedAtIsNullAndIsActive(true, pageable);
    }

    Optional<SupplierEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);

    @Query("SELECT COUNT(s) FROM SupplierEntity s WHERE s.deletedAt IS NULL AND s.isActive = true")
    Long countActiveSuppliers();
}

