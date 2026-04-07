package com.example.spring_ecom.controller.api.file.model;

import java.util.List;

/**
 * Response DTO for file list
 */
public record FileListResponse(
        List<FileItem> files,
        int count
) {
    public static FileListResponse of(List<FileItem> files) {
        return new FileListResponse(files, files.size());
    }
}
