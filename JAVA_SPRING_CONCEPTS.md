# Java & Spring Concepts - Áp Dung Trong Dye An Spring E-Commerce

## 1. Java Records (Java 16+)

### Concept
Record la loai class dac biet danh cho du lieu thuan tuy - immutable data carrier. Compiler tu sinh constructor, getters, equals(), hashCode(), toString().

### Vi du trong du an

**Domain Records:**
```java
// be/landing/src/main/java/com/example/spring_ecom/domain/product/Product.java
public record Product(
    Long id,
    String title,
    String slug,
    String author,
    BigDecimal price,
    BigDecimal discountPrice,
    Integer stockQuantity,
    // ...
) {}
```

**DAO Records:**
```java
// be/landing/src/main/java/com/example/spring_ecom/repository/database/product/dao/ProductWithCategoryDao.java
public record ProductWithCategoryDao(
    Long id,
    String title,
    Long categoryId,
    String categoryName,
    // ...
) {}
```

**Request/Response Records:**
```java
// be/landing/src/main/java/com/example/spring_ecom/controller/api/auth/model/LoginRequest.java
public record LoginRequest(
    String email,
    String password
) {}
```

**Khi nao dung Record:**
- DTO (Data Transfer Objects) giua cac layer
- Value objects (Product, User, Order)
- API request/response models
- DAO (Data Access Objects) cho JPA projections

---

## 2. Optional Class

### Concept
Optional<T> la container co the chua gia tri hoac trong, thay the cho null check truyen thong.

### Vi du trong du an

**Service Layer:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductQueryService.java
public Optional<Product> findById(Long id) {
    return productRepository.findById(id)
            .filter(entity -> Objects.isNull(entity.getDeletedAt()))
            .map(mapper::toDomain);
}

public Optional<Product> findBySlug(String slug) {
    return productRepository.findBySlugAndDeletedAtIsNull(slug)
            .map(mapper::toDomain);
}
```

**Use Case Layer:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductUseCaseService.java
@Override
@Transactional
public Optional<Product> create(Product product) {
    return commandService.create(product);
}
```

**Cach xu ly Optional:**
```java
// orElseThrow - neu khong tim thay thi throw exception
Product product = queryService.findById(id)
        .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));

// isPresent() + ifPresent() - kiem tra va xu ly neu co
if (queryService.existsByProductIdAndUserId(productId, userId)) {
    throw new BaseException(ResponseCode.CONFLICT, "Already exists");
}

// map() - transform gia tri
return repository.findById(userId).map(mapper::toDomain);
```

---

## 3. Stream API & Functional Programming

### Concept
Stream API xu ly du lieu theo phong cach khai bao (declarative). Gom: source, intermediate operations (lazy), terminal operations.

### Vi du trong du an

**Filter & Map:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/order/OrderQueryService.java
BigDecimal totalRevenue = allOrders.stream()
        .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
        .map(OrderEntity::getTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**Sort & ToList:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductCommandService.java
List<CartItem> sortedItems = cartItems.stream()
        .sorted(Comparator.comparing(CartItem::productId))
        .toList();
```

**Reduce:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/order/OrderCommandService.java
BigDecimal subtotal = cartItems.stream()
        .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**Collect to Map:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/order/orderItem/OrderItemCommandService.java
Map<Long, OrderItemEntity> itemMap = orderItems.stream()
        .collect(Collectors.toMap(OrderItemEntity::getId, item -> item));
```

**AnyMatch:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/order/orderItem/OrderItemQueryService.java
public boolean hasActiveItems(List<OrderItemEntity> orderItems) {
    return orderItems.stream()
            .anyMatch(item -> item.getQuantity() > item.getCancelledQuantity());
}
```

---

## 4. @Transactional Annotation

### Concept
Declarative transaction management - Spring tu dong begin, commit, rollback.

### Vi du trong du an

**Read-only Transaction (Query):**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductUseCaseService.java
@Override
@Transactional(readOnly = true)
public Optional<Product> findById(Long id) {
    return queryService.findById(id);
}

@Transactional(readOnly = true)
public Page<Product> findAll(Pageable pageable) {
    return queryService.findAll(pageable);
}
```

**Write Transaction (Command):**
```java
@Override
@Transactional
public Optional<Product> create(Product product) {
    return commandService.create(product);
}

@Transactional
public void delete(Long id) {
    commandService.delete(id);
}
```

**Transaction voi rollback:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/review/ProductReviewUseCaseService.java
@Override
@Transactional
public ProductReview createReview(ProductReview review) {
    if (queryService.existsByProductIdAndUserId(review.productId(), review.userId())) {
        throw new BaseException(ResponseCode.CONFLICT, "You have already reviewed this product");
    }
    return commandService.create(review);
}
```

**Tai sao dung readOnly=true:**
- JPA/Hibernate tat dirty checking - giam overhead
- Database co the toi uu query (read tu replica)
- Flush mode = MANUAL - khong flush session
- Ap dung cho: query methods, reports

---

## 5. Spring Annotations

### @Service
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductQueryService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    // Business logic layer
}
```

### @Repository
```java
// be/landing/src/main/java/com/example/spring_ecom/repository/database/product/ProductRepository.java
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // Data access layer
}
```

### @RequiredArgsConstructor (Lombok)
```java
// Thay the constructor injection
@Service
@RequiredArgsConstructor
public class ProductUseCaseService {
    private final ProductQueryService queryService;      // final field
    private final ProductCommandService commandService;  // final field
    // Lombok tu sinh constructor voi cac final fields
}
```

---

## 6. JPA Repository & Custom Queries

### Concept
Spring Data JPA cung cap abstraction tren JPA, giam boilerplate.

### Vi du trong du an

**Basic JPA Repository:**
```java
// be/landing/src/main/java/com/example/spring_ecom/repository/database/product/ProductRepository.java
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    // Method naming convention - Spring tu sinh query
    Optional<ProductEntity> findBySlugAndDeletedAtIsNull(String slug);
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
```

**Custom JPQL Query:**
```java
@Query("""
    SELECT p FROM ProductEntity p
    LEFT JOIN FETCH p.category c
    WHERE p.deletedAt IS NULL
    AND (:categoryId IS NULL OR p.categoryId = :categoryId)
    AND (:isActive IS NULL OR p.isActive = :isActive)
""")
Page<ProductEntity> findProductsWithFilters(
    @Param("categoryId") Long categoryId,
    @Param("isActive") Boolean isActive,
    Pageable pageable
);
```

**Projection voi Record:**
```java
@Query("""
    SELECT new com.example.spring_ecom.repository.database.product.dao.ProductWithCategoryDao(
           p.id, p.title, p.categoryId, c.name, p.createdAt, p.updatedAt, p.deletedAt)
    FROM ProductEntity p
    LEFT JOIN CategoryEntity c ON p.categoryId = c.id AND c.deletedAt IS NULL
    WHERE p.deletedAt IS NULL
""")
Page<ProductWithCategoryDao> findProductsWithCategoryInfo(
    @Param("categoryId") Long categoryId,
    Pageable pageable
);
```

---

## 7. Exception Handling

### Concept
Custom exception hierarchy voi BaseException, xu ly loi theo response code.

### Vi du trong du an

**BaseException:**
```java
// be/landing/src/main/java/com/example/spring_ecom/core/exception/BaseException.java
public class BaseException extends RuntimeException {
    private final ResponseCode responseCode;
    
    public BaseException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
```

**Su dung trong Service:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductCommandService.java
private ProductEntity findActiveProductById(Long id) {
    return productRepository.findById(id)
            .filter(e -> Objects.isNull(e.getDeletedAt()))
            .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
}

private void validateProduct(Product product) {
    if (Objects.nonNull(product.discountPrice()) && 
        product.discountPrice().compareTo(product.price()) > 0) {
        throw new BaseException(ResponseCode.BAD_REQUEST, 
            "Discount price cannot be greater than price");
    }
}
```

**Global Exception Handler:**
```java
// be/core/src/main/java/com/example/spring_ecom/core/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        // Handle custom exceptions
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(...) {
        // Handle validation errors
    }
}
```

---

## 8. Dependency Injection Patterns

### Constructor Injection (Recommended)
```java
@Service
@RequiredArgsConstructor  // Lombok tu sinh constructor
public class ProductUseCaseService implements ProductUseCase {
    
    private final ProductQueryService queryService;      // Immutable
    private final ProductCommandService commandService;  // Immutable
    private final ProductResponseMapper mapper;          // Immutable
}
```

**Tai sao uu tien Constructor Injection:**
- Immutable dependencies (final fields)
- Khong the thay doi sau khi khoi tao
- De test (co the mock trong unit test)
- Khong can @Autowired annotation (Spring 4.3+ tu dong inject)

### Interface Segregation
```java
// Interface dinh nghia contract
public interface ProductUseCase {
    Optional<Product> findById(Long id);
    Optional<Product> create(Product product);
    void delete(Long id);
}

// Implementation
@Service
@RequiredArgsConstructor
public class ProductUseCaseService implements ProductUseCase {
    // Implementation details
}
```

---

## 9. Soft Delete Pattern

### Concept
Khong xoa du lieu thuc su, danh dau bang deletedAt timestamp.

### Vi du trong du an

**Entity Base Class:**
```java
// BaseAuditEntity co deletedAt field
public abstract class BaseAuditEntity {
    private LocalDateTime deletedAt;
}
```

**Query voi Soft Delete:**
```java
// Chi lay records chua bi xoa
Optional<ProductEntity> findBySlugAndDeletedAtIsNull(String slug);

// Filter trong query
@Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL")
Page<ProductEntity> findAllActive(Pageable pageable);
```

**Soft Delete Implementation:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductCommandService.java
public void delete(Long id) {
    ProductEntity entity = findActiveProductById(id);
    mapper.markAsDeleted(entity, null);  // Set deletedAt = now()
    productRepository.save(entity);      // Update thay vi delete
}
```

---

## 10. CQRS Pattern (Command Query Responsibility Segregation)

### Concept
Tach biet read operations (Query) va write operations (Command).

### Vi du trong du an

**Query Service (Read-only):**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductQueryService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    
    public Page<Product> findAll(Pageable pageable) { ... }
    
    public Optional<Product> findById(Long id) { ... }
    
    public Optional<Product> findBySlug(String slug) { ... }
}
```

**Command Service (Write):**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductCommandService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    
    public Optional<Product> create(Product product) { ... }
    
    public Optional<Product> update(Long id, Product product) { ... }
    
    public void delete(Long id) { ... }
}
```

**Use Case Service (Orchestrator):**
```java
@Service
@RequiredArgsConstructor
public class ProductUseCaseService implements ProductUseCase {
    
    private final ProductQueryService queryService;
    private final ProductCommandService commandService;
    
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return queryService.findById(id);  // Delegate to query
    }
    
    @Transactional
    public Optional<Product> create(Product product) {
        return commandService.create(product);  // Delegate to command
    }
}
```

---

## 11. Pagination & Sorting

### Concept
Spring Data JPA ho tro pagination va sorting qua Pageable interface.

### Vi du trong du an

**Repository voi Pageable:**
```java
Page<ProductEntity> findProductsWithFilters(
    @Param("categoryId") Long categoryId,
    Pageable pageable
);
```

**Service xu ly Pagination:**
```java
public Page<Product> findAll(Pageable pageable) {
    return productRepository.findProductsWithFilters(null, null, null, null, pageable)
            .map(mapper::toDomain);
}
```

**Controller nhan Pageable:**
```java
@GetMapping
public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id,desc") String sort,
    @RequestParam(required = false) Long categoryId
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
    // ...
}
```

---

## 12. Builder Pattern (Lombok @Builder)

### Concept
Tao object phuc tap voi nhieu tham so, cho phep tao tung buoc.

### Vi du trong du an

**Entity voi Builder:**
```java
// be/landing/src/main/java/com/example/spring_ecom/repository/database/review/ReviewReactionEntity.java
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReactionEntity {
    // Fields...
}
```

**Su dung Builder:**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/review/ProductReviewUseCaseService.java
ReviewReactionEntity newReaction = ReviewReactionEntity.builder()
        .reviewId(reviewId)
        .userId(userId)
        .reactionType(reactionType)
        .build();
```

---

## 13. Validation (Jakarta Validation)

### Concept
Validate input voi annotations @NotBlank, @Size, @Email, etc.

### Vi du trong du an

**Request Record voi Validation:**
```java
// be/landing/src/main/java/com/example/spring_ecom/controller/api/category/model/CategoryRequest.java
public record CategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    String name,
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {}

// be/landing/src/main/java/com/example/spring_ecom/controller/api/user/model/ChangePasswordRequest.java
public record ChangePasswordRequest(
    @NotBlank(message = "Current password is required")
    String currentPassword,
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    String newPassword,
    
    @NotBlank(message = "Confirm password is required")
    String confirmPassword
) {}
```

---

## 14. Logging (SLF4J + Lombok)

### Concept
Logging voi SLF4J, Lombok @Slf4j tu dong tao logger.

### Vi du trong du an

**Su dung @Slf4j:**
```java
@Slf4j
@Service
public class ProductCommandService {
    
    public void validateStockForOrder(List<CartItem> cartItems) {
        log.info("Validating stock for {} cart items", cartItems.size());
        
        if (Objects.isNull(product)) {
            log.warn("Product not found in client DB: productId={}", cartItem.productId());
        }
        
        log.info("Stock validation passed for {} cart items", cartItems.size());
    }
}
```

---

## 15. Objects Utility Class

### Concept
Java 7+ utility class cho null-safe operations.

### Vi du trong du an

**Objects.isNull() & Objects.nonNull():**
```java
// be/landing/src/main/java/com/example/spring_ecom/service/product/ProductCommandService.java
if (Objects.isNull(categoryId)) return;

if (Objects.nonNull(product.discountPrice()) && 
    product.discountPrice().compareTo(product.price()) > 0) {
    throw new BaseException(...);
}

// Filter voi Objects.isNull
return productRepository.findById(id)
        .filter(entity -> Objects.isNull(entity.getDeletedAt()))
        .map(mapper::toDomain);
```

---

## Summary Table

| Concept | Location | Purpose |
|---------|----------|---------|
| **Record** | `domain/*`, `dao/*`, `model/*` | Immutable DTO, Value Objects |
| **Optional** | `service/*`, `repository/*` | Null-safe operations |
| **Stream API** | `service/*/OrderQueryService`, `ProductCommandService` | Data processing |
| **@Transactional** | `service/*UseCaseService` | Transaction management |
| **@Service** | `service/*` | Business logic layer |
| **@Repository** | `repository/database/*` | Data access layer |
| **JPA Repository** | `repository/database/*Repository.java` | CRUD operations |
| **Custom Query** | `repository/database/*Repository.java` | Complex queries |
| **BaseException** | `core/exception/*` | Custom exception handling |
| **Constructor Injection** | All services | Dependency injection |
| **Soft Delete** | `BaseAuditEntity`, all repositories | Data preservation |
| **CQRS** | `*QueryService`, `*CommandService` | Separate read/write |
| **Pagination** | Controllers, Services, Repositories | Large dataset handling |
| **@Builder** | Entity classes | Object creation |
| **Validation** | Request models | Input validation |
| **@Slf4j** | Services | Logging |

---

## Interview Tips

Khi bi hoi "Em dang su dung technique nay o dau trong du an?", cau tra loi:

1. **Noi vi tri cu the** - file path, class name, method name
2. **Giai thich tai sao dung** - loi ich, van de giai quyet
3. **So sanh voi cach khac** - tai sao cach nay tot hon
4. **Noi ve ket qua** - performance, maintainability, testability

**Vi du:**
> "Em su dung Record trong domain layer de tao cac value objects nhu Product, User, Order. Dieu nay giam boilerplate code - khong can viet getter, setter, equals, hashCode. Record la immutable, dam bao thread-safe va de test. Trong du an co khoang 20+ records duoc su dung cho DTO, DAO, va API models."
