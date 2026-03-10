package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductUseCaseService implements ProductUseCase {
    
    private final ProductQueryService queryService;
    private final ProductCommandService commandService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return queryService.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return queryService.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findBySlug(String slug) {
        return queryService.findBySlug(slug);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Product> findByCategorySlug(String slug, Pageable pageable) {
        return queryService.findByCategorySlug(slug, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return queryService.searchProducts(keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> findBestsellerProducts(Pageable pageable) {
        return queryService.findBestsellerProducts(pageable);
    }
    
    @Override
    @Transactional
    public Optional<Product> create(Product product) {
        return commandService.create(product);
    }
    
    @Override
    @Transactional
    public Optional<Product> update(Long id, Product product) {
        return commandService.update(id, product);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        commandService.delete(id);
    }
}
