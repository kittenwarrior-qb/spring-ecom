package com.example.spring_ecom.controller.grpc.product;

import com.example.spring_ecom.grpc.services.ProductServiceGrpc;
import com.example.spring_ecom.grpc.services.ProductServiceProto.*;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.service.product.ProductUseCase;
import com.example.spring_ecom.service.category.CategoryUseCase;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.category.Category;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    
    private final ProductUseCase productUseCase;
    private final CategoryUseCase categoryUseCase;
    private final ProductGrpcMapper productGrpcMapper;
    
    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<CreateProductResponse> responseObserver) {
        try {
            log.info("gRPC CreateProduct called");
            
            Product product = productGrpcMapper.toDomain(request.getProduct());
            Product createdProduct = productUseCase.create(product)
                    .orElseThrow(() -> new RuntimeException("Failed to create product"));
            
            ProductProto.Product productProto = productGrpcMapper.toProto(createdProduct);
            
            CreateProductResponse response = CreateProductResponse.newBuilder()
                    .setProduct(productProto)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in CreateProduct gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<UpdateProductResponse> responseObserver) {
        try {
            log.info("gRPC UpdateProduct called for productId: {}", request.getProductId());
            
            Product product = productGrpcMapper.toDomain(request.getProduct());
            Product updatedProduct = productUseCase.update(request.getProductId(), product)
                    .orElseThrow(() -> new RuntimeException("Failed to update product"));
            
            ProductProto.Product productProto = productGrpcMapper.toProto(updatedProduct);

            UpdateProductResponse response = UpdateProductResponse.newBuilder()
                    .setProduct(productProto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in UpdateProduct gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        try {
            log.info("gRPC DeleteProduct called for productId: {}", request.getProductId());
            
            productUseCase.delete(request.getProductId());
            
            DeleteProductResponse response = DeleteProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted successfully")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in DeleteProduct gRPC call", ex);
            
            DeleteProductResponse errorResponse = DeleteProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to delete product: " + ex.getMessage())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void getProductsAdmin(GetProductsAdminRequest request, StreamObserver<GetProductsAdminResponse> responseObserver) {
        try {
            log.info("gRPC GetProductsAdmin called with page: {}, size: {}", 
                    request.getPageRequest().getPage(), request.getPageRequest().getSize());
            
            Pageable pageable = PageRequest.of(
                    request.getPageRequest().getPage(),
                    request.getPageRequest().getSize()
            );
            
            String search = request.getSearch();
            Page<Product> productPage;
            if (Objects.nonNull(search) && !search.trim().isEmpty()) {
                productPage = productUseCase.searchProducts(search, pageable);
            } else {
                productPage = productUseCase.findAll(pageable);
            }
            
            GetProductsAdminResponse.Builder responseBuilder = GetProductsAdminResponse.newBuilder();
            
            // Add products
            productPage.getContent().forEach(product -> {
                ProductProto.Product productProto = productGrpcMapper.toProto(product);
                responseBuilder.addProducts(productProto);
            });
            
            // Add page info
            responseBuilder.setPageResponse(
                    com.example.spring_ecom.grpc.common.CommonProto.PageResponse.newBuilder()
                            .setPage(productPage.getNumber())
                            .setSize(productPage.getSize())
                            .setTotalElements(productPage.getTotalElements())
                            .setTotalPages(productPage.getTotalPages())
                            .setFirst(productPage.isFirst())
                            .setLast(productPage.isLast())
                            .build()
            );
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetProductsAdmin gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void incrementProductViews(IncrementProductViewsRequest request, StreamObserver<IncrementProductViewsResponse> responseObserver) {
        try {
            log.info("gRPC IncrementProductViews called for productId: {}", request.getProductId());
            
            // Note: Add logic to increment views via ProductUseCase if supported
            // productUseCase.incrementViews(request.getProductId());
            
            IncrementProductViewsResponse response = IncrementProductViewsResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in IncrementProductViews gRPC call", ex);
            
            IncrementProductViewsResponse response = IncrementProductViewsResponse.newBuilder()
                    .setSuccess(false)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void getProductById(GetProductByIdRequest request, StreamObserver<GetProductByIdResponse> responseObserver) {
        try {
            log.info("gRPC GetProductById called for productId: {}", request.getProductId());
            java.util.Optional<Product> product = productUseCase.findById(request.getProductId());
            
            if (product.isPresent()) {
                GetProductByIdResponse response = GetProductByIdResponse.newBuilder()
                        .setProduct(productGrpcMapper.toProto(product.get()))
                        .setFound(true)
                        .build();
                responseObserver.onNext(response);
            } else {
                GetProductByIdResponse response = GetProductByIdResponse.newBuilder()
                        .setFound(false)
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in GetProductById gRPC call", ex);
            responseObserver.onNext(GetProductByIdResponse.newBuilder().setFound(false).build());
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void validateProductAvailability(ValidateProductAvailabilityRequest request, StreamObserver<ValidateProductAvailabilityResponse> responseObserver) {
        try {
            log.info("gRPC ValidateProductAvailability called for productId: {}, qty: {}",
                    request.getProductId(), request.getQuantity());
            
            java.util.Optional<Product> productOpt = productUseCase.findById(request.getProductId());
            log.info("stopped");
            if (productOpt.isEmpty()) {
                responseObserver.onNext(ValidateProductAvailabilityResponse.newBuilder()
                        .setAvailable(false)
                        .setStockQuantity(0)
                        .setMessage("Product not found")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            
            Product product = productOpt.get();
            boolean available = product.stockQuantity() >= request.getQuantity();
            
            responseObserver.onNext(ValidateProductAvailabilityResponse.newBuilder()
                    .setAvailable(available)
                    .setStockQuantity(product.stockQuantity())
                    .setMessage(available ? "In stock" : "Insufficient stock. Available: " + product.stockQuantity())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in ValidateProductAvailability gRPC call", ex);
            responseObserver.onNext(ValidateProductAvailabilityResponse.newBuilder()
                    .setAvailable(false)
                    .setMessage("Error: " + ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void updateProductStock(UpdateProductStockRequest request, StreamObserver<UpdateProductStockResponse> responseObserver) {
        try {
            log.info("gRPC UpdateProductStock called for productId: {}, delta: {}",
                    request.getProductId(), request.getQuantityDelta());
            
            int newStock = productUseCase.updateProductStock(request.getProductId(), request.getQuantityDelta());
            
            responseObserver.onNext(UpdateProductStockResponse.newBuilder()
                    .setSuccess(true)
                    .setNewStockQuantity(newStock)
                    .setMessage("Stock updated successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in UpdateProductStock gRPC call", ex);
            responseObserver.onNext(UpdateProductStockResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed: " + ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void updateProductsSoldCount(UpdateProductsSoldCountRequest request, StreamObserver<UpdateProductsSoldCountResponse> responseObserver) {
        try {
            log.info("gRPC UpdateProductsSoldCount called for {} products", request.getItemsCount());
            
            java.util.Map<Long, Integer> soldMap = new java.util.HashMap<>();
            request.getItemsList().forEach(item ->
                soldMap.put(item.getProductId(), item.getQuantityDelta())
            );
            
            productUseCase.updateProductsSoldCount(soldMap);
            
            responseObserver.onNext(UpdateProductsSoldCountResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in UpdateProductsSoldCount gRPC call", ex);
            responseObserver.onNext(UpdateProductsSoldCountResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createCategory(CreateCategoryRequest request, StreamObserver<CreateCategoryResponse> responseObserver) {
        try {
            log.info("gRPC CreateCategory called");
            
            Category category = productGrpcMapper.toDomain(request.getCategory());
            Category createdCategory = categoryUseCase.create(category)
                    .orElseThrow(() -> new RuntimeException("Failed to create category"));
            
            ProductProto.Category categoryProto = productGrpcMapper.toProto(createdCategory);
            
            CreateCategoryResponse response = CreateCategoryResponse.newBuilder()
                    .setCategory(categoryProto)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in CreateCategory gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void updateCategory(UpdateCategoryRequest request, StreamObserver<UpdateCategoryResponse> responseObserver) {
        try {
            log.info("gRPC UpdateCategory called for categoryId: {}", request.getCategoryId());
            
            Category category = productGrpcMapper.toDomain(request.getCategory());
            Category updatedCategory = categoryUseCase.update(request.getCategoryId(), category)
                    .orElseThrow(() -> new RuntimeException("Failed to update category"));
            
            ProductProto.Category categoryProto = productGrpcMapper.toProto(updatedCategory);
            
            UpdateCategoryResponse response = UpdateCategoryResponse.newBuilder()
                    .setCategory(categoryProto)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in UpdateCategory gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void deleteCategory(DeleteCategoryRequest request, StreamObserver<DeleteCategoryResponse> responseObserver) {
        try {
            log.info("gRPC DeleteCategory called for categoryId: {}", request.getCategoryId());
            
            categoryUseCase.delete(request.getCategoryId());
            
            DeleteCategoryResponse response = DeleteCategoryResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Category deleted successfully")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in DeleteCategory gRPC call", ex);
            
            DeleteCategoryResponse errorResponse = DeleteCategoryResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to delete category: " + ex.getMessage())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void getCategoriesAdmin(GetCategoriesAdminRequest request, StreamObserver<GetCategoriesAdminResponse> responseObserver) {
        try {
            log.info("gRPC GetCategoriesAdmin called with page: {}, size: {}", 
                    request.getPageRequest().getPage(), request.getPageRequest().getSize());
            
            Pageable pageable = PageRequest.of(
                    request.getPageRequest().getPage(),
                    request.getPageRequest().getSize()
            );
            
            String search = request.getSearch().isEmpty() ? null : request.getSearch();
            Boolean isActive = null; // TODO: Add isActive filter to proto if needed
            
            Page<Category> categoryPage = categoryUseCase.findAllWithFilters(pageable, search, isActive);
            
            GetCategoriesAdminResponse.Builder responseBuilder = GetCategoriesAdminResponse.newBuilder();
            
            // Add categories
            categoryPage.getContent().forEach(category -> {
                ProductProto.Category categoryProto = productGrpcMapper.toProto(category);
                responseBuilder.addCategories(categoryProto);
            });
            
            // Add page info
            responseBuilder.setPageResponse(
                    com.example.spring_ecom.grpc.common.CommonProto.PageResponse.newBuilder()
                            .setPage(categoryPage.getNumber())
                            .setSize(categoryPage.getSize())
                            .setTotalElements(categoryPage.getTotalElements())
                            .setTotalPages(categoryPage.getTotalPages())
                            .setFirst(categoryPage.isFirst())
                            .setLast(categoryPage.isLast())
                            .build()
            );
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetCategoriesAdmin gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
}