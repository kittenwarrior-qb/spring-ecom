package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.repository.database.category.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "author", nullable = false)
    private String author;
    
    @Column(name = "publisher")
    private String publisher;
    
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    @Column(name = "language", nullable = false, length = 50)
    @Builder.Default
    private String language = "Vietnamese";
    
    @Column(name = "pages")
    private Integer pages;
    
    @Column(name = "format", nullable = false, length = 50)
    @Builder.Default
    private String format = "Paperback";
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    @Column(name = "is_bestseller", nullable = false)
    @Builder.Default
    private Boolean isBestseller = false;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;
    
    @Column(name = "sold_count", nullable = false)
    @Builder.Default
    private Integer soldCount = 0;
    
    @Column(name = "rating_average", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAverage = BigDecimal.ZERO;
    
    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<CategoryEntity> categories = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
