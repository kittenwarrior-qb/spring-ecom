package com.example.spring_ecom.service.file;

import com.example.spring_ecom.controller.api.file.model.FileUploadResponse;
import com.example.spring_ecom.controller.api.file.model.UploadFileRequest;

import java.io.InputStream;
import java.util.List;

/**
 * File UseCase Interface
 * Defines all file operations using MinIO
 */
public interface FileUseCase {

    // Command methods
    FileUploadResponse uploadWithPublicUrl(UploadFileRequest request);

    FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request);

    FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request, int expirySeconds);

    void deleteFile(String filename);

    // Query methods
    String getPresignedUrl(String filename);

    String getPresignedUrl(String filename, int expirySeconds);

    List<String> listFiles();

    InputStream downloadFile(String filename);
}
