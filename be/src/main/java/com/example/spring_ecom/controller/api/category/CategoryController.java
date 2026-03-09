package com.example.spring_ecom.controller.api.category;

import com.example.spring_ecom.controller.api.category.model.CategoryRequest;
import com.example.spring_ecom.controller.api.category.model.CategoryRequestMapper;
import com.example.spring_ecom.controller.api.category.model.CategoryResponse;
import com.example.spring_ecom.controller.api.category.model.CategoryResponseMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.category.CategoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {
    
    private final CategoryUseCase categoryUseCase;
    private final CategoryRequestMapper requestMapper;
    private final CategoryResponseMapper responseMapper;
    
    @Override
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryUseCase.findAll()
                .stream()
                .map(responseMapper::toResDto)
                .toList();
        return ApiResponse.Success.of(categories);
    }
    
    @Override
    public ApiResponse<CategoryResponse> getCategoryById(Long id) {
        CategoryResponse category = categoryUseCase.findById(id)
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
        return ApiResponse.Success.of(category);
    }
    
    @Override
    public ApiResponse<CategoryResponse> getCategoryBySlug(String slug) {
        CategoryResponse category = categoryUseCase.findBySlug(slug)
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
        return ApiResponse.Success.of(category);
    }
    
    @Override
    public ApiResponse<List<CategoryResponse>> getCategoriesByParentId(Long parentId) {
        List<CategoryResponse> categories = categoryUseCase.findByParentId(parentId)
                .stream()
                .map(responseMapper::toResDto)
                .toList();
        return ApiResponse.Success.of(categories);
    }
    
    @Override
    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        CategoryResponse category = categoryUseCase.create(requestMapper.toDomain(request))
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Failed to create category"));
        return ApiResponse.Success.of(category);
    }
    
    @Override
    public ApiResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        CategoryResponse category = categoryUseCase.update(id, requestMapper.toDomain(request))
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Category not found"));
        return ApiResponse.Success.of(category);
    }
    
    @Override
    public ApiResponse<Void> deleteCategory(Long id) {
        categoryUseCase.delete(id);
        return ApiResponse.Success.of(null);
    }
}
