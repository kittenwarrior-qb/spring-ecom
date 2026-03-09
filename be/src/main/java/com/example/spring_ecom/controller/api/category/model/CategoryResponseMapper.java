package com.example.spring_ecom.controller.api.category.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CategoryResponseMapper extends BaseModelMapper<CategoryResponse, Category> {
    
    @Mapping(target = "parentName", ignore = true)
    CategoryResponse toResDto(Category domain);
}
