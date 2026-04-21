package com.example.spring_ecom.service.product;

import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.domain.product.StockReceiveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductUseCaseService implements ProductUseCase {
    
    private final ProductQueryService queryService;
    private final ProductCommandService commandService;
    private final ProductResponseMapper productResponseMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return queryService.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductWithCategory> findAllWithCategory(Pageable pageable) {
        return queryService.findAllWithCategory(pageable);
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
    
    @Override
    @Transactional
    public int updateProductStock(Long productId, int delta) {
        return commandService.updateProductStock(productId, delta);
    }
    
    @Override
    @Transactional
    public void updateProductsSoldCount(java.util.Map<Long, Integer> items) {
        commandService.updateProductsSoldCount(items);
    }

    @Override
    @Transactional
    public StockReceiveResult receiveStock(Long productId, int quantityAdded, BigDecimal unitCost) {
        return commandService.receiveStock(productId, quantityAdded, unitCost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllByIds(Collection<Long> ids) {
        return commandService.findAllByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveProducts() {
        return commandService.countActiveProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countLowStockProducts() {
        return commandService.countLowStockProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOutOfStockProducts() {
        return commandService.countOutOfStockProducts();
    }

    @Override
    @Transactional
    public int reserveStock(Long productId, int quantity) {
        return commandService.reserveStock(productId, quantity);
    }

    @Override
    @Transactional
    public void releaseReservedStock(Long productId, int quantity) {
        commandService.releaseReservedStock(productId, quantity);
    }

    @Override
    @Transactional
    public void deductReservedStock(Long productId, int quantity) {
        commandService.deductReservedStock(productId, quantity);
    }
}
