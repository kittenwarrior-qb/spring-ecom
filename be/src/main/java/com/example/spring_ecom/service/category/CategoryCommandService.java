package com.example.spring_ecom.service.category;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SlugUtil;
import com.example.spring_ecom.domain.category.Category;
import com.example.spring_ecom.repository.database.category.CategoryEntity;
import com.example.spring_ecom.repository.database.category.CategoryEntityMapper;
import com.example.spring_ecom.repository.database.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper mapper;
    
    public Optional<Category> create(Category category) {
        // Check if parent exists
        if (category.parentId() != null) {
            categoryRepository.findById(category.parentId())
                    .filter(entity -> entity.getDeletedAt() == null)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Parent category not found"));
        }
        
        CategoryEntity entity = mapper.toEntity(category);
        
        // Auto-generate slug from name if not provided
        if (entity.getSlug() == null || entity.getSlug().isBlank()) {
            String baseSlug = SlugUtil.toSlug(entity.getName());
            String uniqueSlug = generateUniqueSlug(baseSlug);
            entity.setSlug(uniqueSlug);
        } else {
            // If slug is provided, check if it already exists
            if (categoryRepository.existsBySlugAndDeletedAtIsNull(entity.getSlug())) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "Category slug already exists");
            }
        }
        
        // Set default values
        if (entity.getDisplayOrder() == null) {
            entity.setDisplayOrder(0);
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        
        CategoryEntity saved = categoryRepository.save(entity);
        return Optional.of(mapper.toDomain(saved));
    }
    
    public Optional<Category> update(Long id, Category category) {
        CategoryEntity entity = categoryRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
        
        // Check if slug is changed and already exists
        if (!entity.getSlug().equals(category.slug()) && 
            categoryRepository.existsBySlugAndDeletedAtIsNull(category.slug())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Category slug already exists");
        }
        
        // Check if parent exists
        if (category.parentId() != null) {
            // Cannot set itself as parent
            if (category.parentId().equals(id)) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "Category cannot be its own parent");
            }
            
            categoryRepository.findById(category.parentId())
                    .filter(e -> e.getDeletedAt() == null)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Parent category not found"));
        }
        
        // Update fields
        mapper.update(entity, category);
        
        CategoryEntity updated = categoryRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    public void delete(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
        
        // Soft delete
        entity.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(entity);
    }
    
    /**
     * Generate unique slug by appending suffix if needed
     */
    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int suffix = 0;
        
        while (categoryRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            suffix++;
            slug = SlugUtil.toSlugWithSuffix(baseSlug, suffix);
        }
        
        return slug;
    }
}
