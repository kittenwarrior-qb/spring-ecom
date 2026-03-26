package com.example.spring_ecom.repository.grpc.product;

import com.example.spring_ecom.grpc.services.ProductServiceGrpc;
import com.example.spring_ecom.grpc.services.ProductServiceProto.*;
import com.example.spring_ecom.grpc.domain.ProductProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductGrpcClient {
    
    @GrpcClient("core-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;
    
    public List<ProductProto.Product> getProductsAdmin(int page, int size) {
        try {
            log.info("Calling gRPC GetProductsAdmin with page: {}, size: {}", page, size);
            
            GetProductsAdminRequest request = GetProductsAdminRequest.newBuilder()
                    .setPageRequest(
                            com.example.spring_ecom.grpc.common.CommonProto.PageRequest.newBuilder()
                                    .setPage(page)
                                    .setSize(size)
                                    .build()
                    )
                    .build();
            
            GetProductsAdminResponse response = productServiceStub.getProductsAdmin(request);
            return response.getProductsList();
            
        } catch (Exception ex) {
            log.error("Error calling gRPC GetProductsAdmin", ex);
            return List.of();
        }
    }
    
    public boolean validateProductAvailability(Long productId, Integer quantity) {
        try {
            log.info("Validating product availability via gRPC for productId: {}, quantity: {}",
                    productId, quantity);
            ValidateProductAvailabilityRequest request = ValidateProductAvailabilityRequest.newBuilder()
                    .setProductId(productId)
                    .setQuantity(quantity)
                    .build();
            ValidateProductAvailabilityResponse response = productServiceStub.validateProductAvailability(request);
            log.info("stopped");
            return response.getAvailable();
        } catch (Exception ex) {
            log.error("Error validating product availability via gRPC", ex);
            return false;
        }
    }
    
    public Optional<ProductProto.Product> getProductById(Long productId) {
        try {
            log.info("Getting product by id via gRPC for productId: {}", productId);
            GetProductByIdRequest request = GetProductByIdRequest.newBuilder()
                    .setProductId(productId)
                    .build();
            GetProductByIdResponse response = productServiceStub.getProductById(request);
            return response.getFound() ? Optional.of(response.getProduct()) : Optional.empty();
        } catch (Exception ex) {
            log.error("Error getting product by id via gRPC", ex);
            return Optional.empty();
        }
    }
    
    public boolean updateProductStock(Long productId, int delta) {
        try {
            log.info("Updating product stock via gRPC for productId: {}, delta: {}", productId, delta);
            UpdateProductStockRequest request = UpdateProductStockRequest.newBuilder()
                    .setProductId(productId)
                    .setQuantityDelta(delta)
                    .build();
            UpdateProductStockResponse response = productServiceStub.updateProductStock(request);
            if (!response.getSuccess()) {
                throw new RuntimeException(response.getMessage());
            }
            return response.getSuccess();
        } catch (Exception ex) {
            log.error("Error updating product stock via gRPC", ex);
            throw new RuntimeException("Failed to update stock: " + ex.getMessage(), ex);
        }
    }
    
    public void updateProductsSoldCount(java.util.Map<Long, Integer> items) {
        try {
            log.info("Updating products sold count via gRPC for {} products", items.size());
            UpdateProductsSoldCountRequest.Builder builder = UpdateProductsSoldCountRequest.newBuilder();
            items.forEach((productId, qty) ->
                builder.addItems(StockUpdateItem.newBuilder().setProductId(productId).setQuantityDelta(qty).build())
            );
            productServiceStub.updateProductsSoldCount(builder.build());
        } catch (Exception ex) {
            log.error("Error updating products sold count via gRPC", ex);
        }
    }
    
    public void incrementProductViews(Long productId) {
        try {
            log.info("Incrementing product views via gRPC for productId: {}", productId);
            
            IncrementProductViewsRequest request = IncrementProductViewsRequest.newBuilder()
                    .setProductId(productId)
                    .build();
            
            productServiceStub.incrementProductViews(request);
            
        } catch (Exception ex) {
            log.error("Error incrementing product views via gRPC", ex);
        }
    }
    
    public Optional<ProductProto.Product> createProduct(ProductProto.Product product) {
        try {
            log.info("Calling gRPC CreateProduct");
            
            CreateProductRequest request = CreateProductRequest.newBuilder()
                    .setProduct(product)
                    .build();
            
            CreateProductResponse response = productServiceStub.createProduct(request);
            return Optional.of(response.getProduct());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC CreateProduct", ex);
            return Optional.empty();
        }
    }
    
    public Optional<ProductProto.Product> updateProduct(Long productId, ProductProto.Product product) {
        try {
            log.info("Calling gRPC UpdateProduct for productId: {}", productId);
            
            UpdateProductRequest request = UpdateProductRequest.newBuilder()
                    .setProductId(productId)
                    .setProduct(product)
                    .build();
            
            UpdateProductResponse response = productServiceStub.updateProduct(request);
            return Optional.of(response.getProduct());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC UpdateProduct", ex);
            return Optional.empty();
        }
    }
    
    public boolean deleteProduct(Long productId) {
        try {
            log.info("Calling gRPC DeleteProduct for productId: {}", productId);
            
            DeleteProductRequest request = DeleteProductRequest.newBuilder()
                    .setProductId(productId)
                    .build();
            
            DeleteProductResponse response = productServiceStub.deleteProduct(request);
            return response.getSuccess();
            
        } catch (Exception ex) {
            log.error("Error calling gRPC DeleteProduct", ex);
            return false;
        }
    }
    
    public Optional<ProductProto.Category> createCategory(ProductProto.Category category) {
        try {
            log.info("Calling gRPC CreateCategory");
            
            CreateCategoryRequest request = CreateCategoryRequest.newBuilder()
                    .setCategory(category)
                    .build();
            
            CreateCategoryResponse response = productServiceStub.createCategory(request);
            return Optional.of(response.getCategory());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC CreateCategory", ex);
            return Optional.empty();
        }
    }
    
    public Optional<ProductProto.Category> updateCategory(Long categoryId, ProductProto.Category category) {
        try {
            log.info("Calling gRPC UpdateCategory for categoryId: {}", categoryId);
            
            UpdateCategoryRequest request = UpdateCategoryRequest.newBuilder()
                    .setCategoryId(categoryId)
                    .setCategory(category)
                    .build();
            
            UpdateCategoryResponse response = productServiceStub.updateCategory(request);
            return Optional.of(response.getCategory());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC UpdateCategory", ex);
            return Optional.empty();
        }
    }
    
    public boolean deleteCategory(Long categoryId) {
        try {
            log.info("Calling gRPC DeleteCategory for categoryId: {}", categoryId);
            
            DeleteCategoryRequest request = DeleteCategoryRequest.newBuilder()
                    .setCategoryId(categoryId)
                    .build();
            
            DeleteCategoryResponse response = productServiceStub.deleteCategory(request);
            return response.getSuccess();
            
        } catch (Exception ex) {
            log.error("Error calling gRPC DeleteCategory", ex);
            return false;
        }
    }
    
    public List<ProductProto.Category> getCategoriesAdmin(int page, int size, String search, Boolean isActive) {
        try {
            log.info("Calling gRPC GetCategoriesAdmin with page: {}, size: {}", page, size);
            
            GetCategoriesAdminRequest.Builder requestBuilder = GetCategoriesAdminRequest.newBuilder()
                    .setPageRequest(
                            com.example.spring_ecom.grpc.common.CommonProto.PageRequest.newBuilder()
                                    .setPage(page)
                                    .setSize(size)
                                    .build()
                    );
            
            if (search != null && !search.isEmpty()) {
                requestBuilder.setSearch(search);
            }
            if (isActive != null) {
                requestBuilder.setIsActive(isActive);
            }
            
            GetCategoriesAdminResponse response = productServiceStub.getCategoriesAdmin(requestBuilder.build());
            return response.getCategoriesList();
            
        } catch (Exception ex) {
            log.error("Error calling gRPC GetCategoriesAdmin", ex);
            return List.of();
        }
    }
}