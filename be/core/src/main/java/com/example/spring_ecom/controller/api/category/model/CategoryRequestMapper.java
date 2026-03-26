package com.example.spring_ecom.controller.api.category.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.category.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface CategoryRequestMapper extends BaseModelMapper<CategoryRequest, Category> {
}
