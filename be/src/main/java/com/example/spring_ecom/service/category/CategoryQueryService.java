package com.example.spring_ecom.service.category;

import com.example.spring_ecom.domain.category.Category;
import com.example.spring_ecom.repository.database.category.CategoryEntityMapper;
import com.example.spring_ecom.repository.database.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper mapper;
    
    public List<Category> findAll() {
        return categoryRepository.findByDeletedAtIsNullOrderByDisplayOrderAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(mapper::toDomain);
    }
    
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlugAndDeletedAtIsNull(slug)
                .map(mapper::toDomain);
    }
    
    public List<Category> findByParentId(Long parentId) {
        return categoryRepository.findByParentIdAndDeletedAtIsNullOrderByDisplayOrderAsc(parentId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    public List<Category> findActiveCategories() {
        return categoryRepository.findByIsActiveAndDeletedAtIsNullOrderByDisplayOrderAsc(true)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
