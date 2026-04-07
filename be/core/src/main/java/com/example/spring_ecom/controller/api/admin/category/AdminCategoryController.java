package com.example.spring_ecom.controller.api.admin.category;

import com.example.spring_ecom.controller.api.category.model.AdminCategoryRequestMapper;
import com.example.spring_ecom.controller.api.category.model.CategoryResponse;
import com.example.spring_ecom.controller.api.category.model.CategoryResponseMapper;
import com.example.spring_ecom.controller.api.category.model.CreateCategoryRequest;
import com.example.spring_ecom.controller.api.category.model.UpdateCategoryRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.category.Category;
import com.example.spring_ecom.service.category.CategoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Admin Category Management Controller
 * SERVER SERVICE - APIs cho admin quản lý categories
 * Trực tiếp truy xuất DB, không qua gRPC
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Category Management", description = "Admin APIs for managing book categories")
public class AdminCategoryController implements AdminCategoryAPI {

    private final CategoryUseCase categoryUseCase;
    private final CategoryResponseMapper categoryResponseMapper;
    private final AdminCategoryRequestMapper requestMapper;

    @Override
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        try {
            log.info("Admin getting all categories");
            List<Category> categories = categoryUseCase.findAll();
            List<CategoryResponse> responses = categories.stream()
                    .map(categoryResponseMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get categories"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategoriesPaged(
            Pageable pageable,
            String search,
            Boolean isActive) {
        try {
            log.info("Admin getting categories with pagination");
            Page<Category> categories = categoryUseCase.findAllWithFilters(pageable, search, isActive);
            Page<CategoryResponse> responses = categories.map(categoryResponseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get categories"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(Long categoryId) {
        try {
            log.info("Admin getting category by ID: {}", categoryId);
            return categoryUseCase.findById(categoryId)
                    .map(cat -> ResponseEntity.ok(ApiResponse.Success.of(categoryResponseMapper.toResponse(cat))))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get category"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(CreateCategoryRequest request) {
        try {
            log.info("Admin creating new category: {}", request.name());
            Category category = requestMapper.toDomain(request);
            return categoryUseCase.create(category)
                    .map(created -> ResponseEntity.ok(
                            ApiResponse.Success.of(ResponseCode.CREATED, "Category created successfully",
                                    categoryResponseMapper.toResponse(created))))
                    .orElse(ResponseEntity.internalServerError()
                            .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create category")));
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create category: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(Long categoryId, UpdateCategoryRequest request) {
        try {
            log.info("Admin updating category: {}", categoryId);
            Category existing = categoryUseCase.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

            Category updated = requestMapper.mergeWithExisting(request, existing);

            return categoryUseCase.update(categoryId, updated)
                    .map(result -> ResponseEntity.ok(
                            ApiResponse.Success.of(ResponseCode.OK, "Category updated successfully",
                                    categoryResponseMapper.toResponse(result))))
                    .orElse(ResponseEntity.internalServerError()
                            .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update category")));
        } catch (Exception e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update category: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCategory(Long categoryId) {
        try {
            log.info("Admin deleting category: {}", categoryId);
            categoryUseCase.delete(categoryId);
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Category deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete category"));
        }
    }
}