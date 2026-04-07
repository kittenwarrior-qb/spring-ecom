package com.example.spring_ecom.controller.api.category.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper cho admin category request DTOs → domain Category
 *
 * Vì Category là record (immutable), dùng 2-source merge method thay cho @MappingTarget.
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface    AdminCategoryRequestMapper {

    /**
     * Tạo Category domain mới từ CreateCategoryRequest.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Category toDomain(CreateCategoryRequest request);

    /**
     * Merge UpdateCategoryRequest + existing Category → Category mới.
     * Null fields trong request sẽ giữ nguyên từ existing (IGNORE strategy).
     */
    @Mapping(target = "id", source = "existing.id")
    @Mapping(target = "slug", source = "existing.slug")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "parentId", source = "request.parentId")
    @Mapping(target = "displayOrder", source = "request.displayOrder")
    @Mapping(target = "isActive", source = "request.isActive")
    @Mapping(target = "createdAt", source = "existing.createdAt")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", source = "existing.deletedAt")
    Category mergeWithExisting(UpdateCategoryRequest request, Category existing);
}
