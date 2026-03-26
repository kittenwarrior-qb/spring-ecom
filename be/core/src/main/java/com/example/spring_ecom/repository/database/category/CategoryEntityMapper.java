package com.example.spring_ecom.repository.database.category;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CategoryEntityMapper extends BaseEntityMapper<Category, CategoryEntity> {
    
    @Override
    @Mapping(target = "displayOrder", defaultValue = "0")
    @Mapping(target = "isActive", defaultValue = "true")
    CategoryEntity toEntity(Category domain);
}
