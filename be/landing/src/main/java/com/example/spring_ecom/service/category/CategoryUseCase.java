package com.example.spring_ecom.service.category;

import com.example.spring_ecom.domain.category.Category;

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
}
