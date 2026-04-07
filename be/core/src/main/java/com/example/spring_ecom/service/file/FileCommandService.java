package com.example.spring_ecom.service.file;

import com.example.spring_ecom.config.MinioConfig;
import com.example.spring_ecom.controller.api.file.model.FileUploadResponse;
import com.example.spring_ecom.controller.api.file.model.UploadFileRequest;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * File Command Service
 * Handles file write operations - no @Transactional (managed by UseCase layer)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommandService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${minio.max-file-size:10485760}")
    private long maxFileSize;

    @Value("${minio.presigned-url-expiry:3600}")
    private int presignedUrlExpirySeconds;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public String uploadFile(String originalFilename, String contentType, long size, InputStream inputStream) {
        validateFile(contentType, size);

        try {
            String extension = Objects.nonNull(originalFilename) && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;

            String finalContentType = Objects.isNull(contentType) || contentType.isBlank()
                    ? "application/octet-stream"
                    : contentType;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(finalContentType)
                            .build()
            );

            log.info("[FILE-CMD] Uploaded file: {} ({} bytes)", fileName, size);
            return fileName;
        } catch (Exception e) {
            log.error("[FILE-CMD] Failed to upload file: {}", e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }

    public void deleteFile(String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(filename)
                            .build()
            );
            log.info("[FILE-CMD] Deleted file: {}", filename);
        } catch (Exception e) {
            log.error("[FILE-CMD] Failed to delete file {}: {}", filename, e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete file: " + e.getMessage());
        }
    }

    // ========== PUBLIC API (Use UploadFileRequest) ==========

    public FileUploadResponse uploadWithPublicUrl(UploadFileRequest request) {
        String filename = uploadFile(
                request.originalFilename(),
                request.contentType(),
                request.size(),
                request.inputStream()
        );
        String publicUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + filename;
        log.info("[FILE-CMD] Uploaded file with public URL: {}", filename);
        return FileUploadResponse.of(filename, publicUrl);
    }

    public FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request) {
        return uploadWithPresignedUrl(request, presignedUrlExpirySeconds);
    }

    public FileUploadResponse uploadWithPresignedUrl(UploadFileRequest request, int expirySeconds) {
        String filename = uploadFile(
                request.originalFilename(),
                request.contentType(),
                request.size(),
                request.inputStream()
        );

        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucket())
                            .object(filename)
                            .expiry(expirySeconds, TimeUnit.SECONDS)
                            .build()
            );
            log.info("[FILE-CMD] Uploaded file with presigned URL: {} (expiry: {}s)", filename, expirySeconds);
            return FileUploadResponse.of(filename, presignedUrl);
        } catch (Exception e) {
            log.error("[FILE-CMD] Failed to generate presigned URL for {}: {}", filename, e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to generate presigned URL: " + e.getMessage());
        }
    }

    private void validateFile(String contentType, long size) {
        if (size > maxFileSize) {
            throw new BaseException(ResponseCode.BAD_REQUEST,
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSize));
        }

        if (Objects.nonNull(contentType) && !contentType.isBlank() && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("[FILE-CMD] File with content type '{}' may not be allowed", contentType);
        }
    }
}
