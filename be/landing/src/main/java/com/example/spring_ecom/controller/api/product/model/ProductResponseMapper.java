package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class ProductResponseMapper implements BaseModelMapper<ProductResponse, Product> {
    
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "coverImageUrl", source = "coverImageUrl", qualifiedByName = "ensureFullUrl")
    public abstract ProductResponse toResponse(ProductEntity entity);
    
    @Mapping(target = "coverImageUrl", source = "coverImageUrl", qualifiedByName = "ensureFullUrl")
    public abstract ProductResponse toResponse(ProductWithCategory domain);
    
    @Override
    @Mapping(target = "coverImageUrl", source = "coverImageUrl", qualifiedByName = "ensureFullUrl")
    public abstract ProductResponse toResponse(Product product);
    
    /**
     * Ensure URL is full URL - if already full URL, return as-is
     * Core module handles the conversion, this is just a safety check
     */
    @Named("ensureFullUrl")
    protected String ensureFullUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        // Already a full URL - return as-is
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        // If filename only, return as-is (Core should have converted it)
        return url;
    }
}
