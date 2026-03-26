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
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper mapper;
    
    // ========================== MAIN METHODS ================================
    
    public Optional<Category> create(Category category) {
        validateParentCategory(category.parentId());
        
        CategoryEntity entity = mapper.toEntity(category);
        handleSlugGeneration(entity);
        
        CategoryEntity saved = categoryRepository.save(entity);
        return Optional.of(mapper.toDomain(saved));
    }
    
    public Optional<Category> update(Long id, Category category) {
        CategoryEntity entity = findActiveCategoryById(id);
        
        validateParentCategory(category.parentId(), id);
        handleSlugUpdate(category.slug(), entity.getSlug());
        
        mapper.update(entity, category);
        
        CategoryEntity updated = categoryRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    public void delete(Long id) {
        CategoryEntity entity = findActiveCategoryById(id);
        entity.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(entity);
    }

    // ========================== SUPPORT METHODS ================================

    private CategoryEntity findActiveCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
    }
    
    private void validateParentCategory(Long parentId) {
        validateParentCategory(parentId, null);
    }
    
    private void validateParentCategory(Long parentId, Long currentId) {
        if (Objects.isNull(parentId)) return;
        
        if (Objects.nonNull(currentId) && parentId.equals(currentId)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Category cannot be its own parent");
        }
        
        categoryRepository.findById(parentId)
                .filter(entity -> Objects.isNull(entity.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Parent category not found"));
    }
    
    private void handleSlugGeneration(CategoryEntity entity) {
        if (Objects.isNull(entity.getSlug()) || entity.getSlug().isBlank()) {
            String baseSlug = SlugUtil.toSlug(entity.getName());
            String uniqueSlug = generateUniqueSlug(baseSlug);
            entity.setSlug(uniqueSlug);
        } else {
            validateSlugUniqueness(entity.getSlug());
        }
    }
    
    private void handleSlugUpdate(String newSlug, String currentSlug) {
        if (Objects.nonNull(newSlug) && !newSlug.equals(currentSlug)) {
            validateSlugUniqueness(newSlug);
        }
    }
    
    private void validateSlugUniqueness(String slug) {
        if (categoryRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Category slug already exists");
        }
    }
    
    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int suffix = 0;
        int maxRetries = 100; 
        
        while (suffix < maxRetries && categoryRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            suffix++;
            slug = SlugUtil.toSlugWithSuffix(baseSlug, suffix);
        }
        
        if (suffix >= maxRetries) {
            slug = baseSlug + "-" + System.currentTimeMillis();
        }
        
        return slug;
    }
}
