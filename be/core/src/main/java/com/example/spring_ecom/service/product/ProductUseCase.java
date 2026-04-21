package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.domain.product.StockReceiveResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductUseCase {
    
    Page<Product> findAll(Pageable pageable);
    
    Page<ProductWithCategory> findAllWithCategory(Pageable pageable);
    
    Optional<Product> findById(Long id);
    
    Optional<Product> findBySlug(String slug);
    
    Page<Product> findByCategorySlug(String slug, Pageable pageable);
    
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    Page<Product> findBestsellerProducts(Pageable pageable);
    
    Optional<Product> create(Product product);
    
    Optional<Product> update(Long id, Product product);
    
    void delete(Long id);
    
    /**
     * Update tồn kho sản phẩm. delta < 0 để trừ, delta > 0 để cộng.
     * @return stock mới sau khi update
     */
    int updateProductStock(Long productId, int delta);
    
    /**
     * Cập nhật soldCount cho nhiều sản phẩm cùng lúc (dùng khi giao hàng thành công).
     * items: map productId -> quantity sold
     */
    void updateProductsSoldCount(java.util.Map<Long, Integer> items);

    /**
     * Nhập hàng: lock row, cộng stock, tính giá vốn bình quân gia quyền.
     * Dùng bởi InventoryCommandService khi nhận đơn nhập hàng (PO).
     */
    StockReceiveResult receiveStock(Long productId, int quantityAdded, BigDecimal unitCost);

    /**
     * Tìm nhiều sản phẩm theo danh sách ID (chỉ trả domain, không lock).
     */
    List<Product> findAllByIds(Collection<Long> ids);

    // ========== Statistics (dùng bởi StatisticsQueryService) ==========

    Long countActiveProducts();

    Long countLowStockProducts();

    Long countOutOfStockProducts();

    // ========== Stock Reservation (dùng bởi OrderEventService) ==========

    /**
     * Reserve stock cho đơn hàng. Trả về available quantity trước khi reserve.
     * @return available quantity (stock - reserved). Nếu < quantity thì không reserve.
     */
    int reserveStock(Long productId, int quantity);

    void releaseReservedStock(Long productId, int quantity);

    void deductReservedStock(Long productId, int quantity);
}
