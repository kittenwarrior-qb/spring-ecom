package com.example.spring_ecom.service.category;

import com.example.spring_ecom.domain.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryUseCase {
    
    List<Category> findAll();
    
    Optional<Category> findById(Long id);
    
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByParentId(Long parentId);
    
    List<Category> findActiveCategories();
    
    Optional<Category> create(Category category);
    
    Optional<Category> update(Long id, Category category);
    
    void delete(Long id);
    
    // New method for gRPC
    Page<Category> findAllWithFilters(Pageable pageable, String search, Boolean isActive);
}
