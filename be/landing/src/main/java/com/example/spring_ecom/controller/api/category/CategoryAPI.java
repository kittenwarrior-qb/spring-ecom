package com.example.spring_ecom.controller.api.category;

import com.example.spring_ecom.controller.api.category.model.CategoryRequest;
import com.example.spring_ecom.controller.api.category.model.CategoryResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category", description = "Category management APIs")
@RequestMapping("/api/categories")
public interface CategoryAPI {
    
    @Operation(summary = "Get all categories")
    @GetMapping
    ApiResponse<List<CategoryResponse>> getAllCategories();
    
    @Operation(summary = "Get category by id")
    @GetMapping("/{id}")
    ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id);
    
    @Operation(summary = "Get category by slug")
    @GetMapping("/slug/{slug}")
    ApiResponse<CategoryResponse> getCategoryBySlug(@PathVariable String slug);
    
    @Operation(summary = "Get categories by parent id")
    @GetMapping("/parent/{parentId}")
    ApiResponse<List<CategoryResponse>> getCategoriesByParentId(@PathVariable Long parentId);
    
    @Operation(summary = "Create new category")
    @PostMapping
    ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request);
    
    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request);
    
    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteCategory(@PathVariable Long id);
}
