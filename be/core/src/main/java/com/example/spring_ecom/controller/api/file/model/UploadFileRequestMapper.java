package com.example.spring_ecom.controller.api.file.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(config = MapStructGlobalConfig.class)
public interface UploadFileRequestMapper {


    default UploadFileRequest toRequest(MultipartFile file) throws IOException {
        return UploadFileRequest.from(file);
    }
}
