package com.example.spring_ecom.controller.api.file.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Request DTO for file upload
 * Wraps MultipartFile and provides convenient accessors
 */
public record UploadFileRequest(
        String originalFilename,
        String contentType,
        long size,
        InputStream inputStream
) {
    public static UploadFileRequest from(MultipartFile file) throws IOException {
        return new UploadFileRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        );
    }
}
