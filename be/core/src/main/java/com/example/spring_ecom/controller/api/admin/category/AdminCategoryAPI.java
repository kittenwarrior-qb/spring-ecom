package com.example.spring_ecom.controller.api.admin.category;

import com.example.spring_ecom.controller.api.category.model.CategoryResponse;
import com.example.spring_ecom.controller.api.category.model.CreateCategoryRequest;
import com.example.spring_ecom.controller.api.category.model.UpdateCategoryRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/api/admin/categories")
@Tag(name = "Admin Category Management", description = "Admin APIs for managing book categories")
public interface AdminCategoryAPI {

    @Operation(summary = "Get all categories", description = "Get all categories")
    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY_VIEW')")
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories();

    @Operation(summary = "Get categories with pagination", description = "Get paginated categories with filters")
    @GetMapping("/paged")
    @PreAuthorize("hasAuthority('CATEGORY_VIEW')")
    ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategoriesPaged(
            Pageable pageable,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive);

    @Operation(summary = "Get category by ID", description = "Get category detail by ID")
    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('CATEGORY_VIEW')")
    ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Long categoryId);

    @Operation(summary = "Create category", description = "Create new book category")
    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request);

    @Operation(summary = "Update category", description = "Update existing category")
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE')")
    ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request);

    @Operation(summary = "Delete category", description = "Soft delete category")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE')")
    ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId);
}
