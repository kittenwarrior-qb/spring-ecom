package com.example.spring_ecom.service.file;

import com.example.spring_ecom.controller.api.file.model.FileItem;
import com.example.spring_ecom.controller.api.file.model.FileListResponse;
import com.example.spring_ecom.controller.api.file.model.FileUploadResponse;
import com.example.spring_ecom.controller.api.file.model.UploadFileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * File UseCase Service
 * Orchestrates file operations - @Transactional at this layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUseCaseService implements FileUseCase {

    private final FileCommandService commandService;
    private final FileQueryService queryService;

    // ========== Command Methods ==========

    @Override
    @Transactional
    public FileUploadResponse uploadWithPublicUrl(UploadFileRequest request) {
        log.info("[FILE-USECASE] Uploading file with public URL: {}", request.originalFilename());
        return commandService.uploadWithPublicUrl(request);
    }

    @Override
    @Transactional
    public FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request) {
        log.info("[FILE-USECASE] Uploading file with presigned URL: {}", request.originalFilename());
        return commandService.uploadWithPresignedUrl(request);
    }

    @Override
    @Transactional
    public FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request, int expirySeconds) {
        log.info("[FILE-USECASE] Uploading file with presigned URL (expiry: {}s): {}", expirySeconds, request.originalFilename());
        return commandService.uploadWithPresignedUrl(request, expirySeconds);
    }

    @Override
    @Transactional
    public void deleteFile(String filename) {
        log.info("[FILE-USECASE] Deleting file: {}", filename);
        commandService.deleteFile(filename);
    }

    // ========== Query Methods ==========

    @Override
    @Transactional(readOnly = true)
    public String getPresignedUrl(String filename) {
        log.info("[FILE-USECASE] Getting presigned URL for: {}", filename);
        return queryService.getPresignedUrl(filename);
    }

    @Override
    @Transactional(readOnly = true)
    public String getPresignedUrl(String filename, int expirySeconds) {
        log.info("[FILE-USECASE] Getting presigned URL (expiry: {}s) for: {}", expirySeconds, filename);
        return queryService.getPresignedUrl(filename, expirySeconds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listFiles() {
        log.info("[FILE-USECASE] Listing files");
        return queryService.listFiles();
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream downloadFile(String filename) {
        log.info("[FILE-USECASE] Downloading file: {}", filename);
        return queryService.downloadFile(filename);
    }

    // ========== Convenience Methods for Controller ==========

    @Transactional(readOnly = true)
    public String getPresignedUrlResponse(String filename, Integer expirySeconds) {
        if (expirySeconds != null) {
            return getPresignedUrl(filename, expirySeconds);
        }
        return getPresignedUrl(filename);
    }

    @Transactional(readOnly = true)
    public FileListResponse listFilesResponse() {
        log.info("[FILE-USECASE] Listing files with presigned URLs");
        List<FileItem> files = queryService.listFilesWithPresignedUrls();
        return FileListResponse.of(files);
    }

    @Transactional(readOnly = true)
    public Resource downloadFileResource(String filename) {
        InputStream stream = downloadFile(filename);
        return new InputStreamResource(stream);
    }
}
