package com.example.spring_ecom.controller.api.file.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for file upload response
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface UploadFileResponseMapper {

    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "url", source = "url")
    FileUploadResponse toResponse(String filename, String url);
}
