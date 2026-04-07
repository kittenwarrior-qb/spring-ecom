package com.example.spring_ecom.controller.api.file.model;

/**
 * Response DTO for file upload
 */
public record FileUploadResponse(
        String filename,
        String url
) {
    public static FileUploadResponse of(String filename, String url) {
        return new FileUploadResponse(filename, url);
    }
}
