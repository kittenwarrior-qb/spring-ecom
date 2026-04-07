package com.example.spring_ecom.controller.api.file.model;

/**
 * File item with filename and presigned URL
 */
public record FileItem(
        String filename,
        String url
) {
    public static FileItem of(String filename, String url) {
        return new FileItem(filename, url);
    }
}
