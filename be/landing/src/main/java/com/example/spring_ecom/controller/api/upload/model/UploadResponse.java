package com.example.spring_ecom.controller.api.upload.model;

public record UploadResponse(
    String url,
    String publicId,
    String format,
    Long size,
    Integer width,
    Integer height
) {
}
