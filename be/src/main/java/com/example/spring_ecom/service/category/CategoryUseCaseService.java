package com.example.spring_ecom.service.category;

import com.example.spring_ecom.domain.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryUseCaseService implements CategoryUseCase {
    
    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return queryService.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return queryService.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String slug) {
        return queryService.findBySlug(slug);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> findByParentId(Long parentId) {
        return queryService.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {
        return queryService.findActiveCategories();
    }
    
    @Override
    @Transactional
    public Optional<Category> create(Category category) {
        return commandService.create(category);
    }
    
    @Override
    @Transactional
    public Optional<Category> update(Long id, Category category) {
        return commandService.update(id, category);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        commandService.delete(id);
    }
}
