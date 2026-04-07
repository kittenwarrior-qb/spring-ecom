package com.example.spring_ecom.service.file;

import com.example.spring_ecom.config.MinioConfig;
import com.example.spring_ecom.controller.api.file.model.FileItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * File Query Service
 * Handles file read operations - no @Transactional (managed by UseCase layer)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileQueryService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${minio.presigned-url-expiry:3600}")
    private int presignedUrlExpirySeconds;

    public InputStream downloadFile(String filename) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(filename)
                            .build()
            );
        } catch (Exception e) {
            log.error("[FILE-QUERY] Failed to download file {}: {}", filename, e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }

    public List<String> listFiles() {
        try {
            List<String> fileNames = new ArrayList<>();
            Iterable<io.minio.Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(minioConfig.getBucket()).build()
            );
            for (io.minio.Result<Item> item : items) {
                fileNames.add(item.get().objectName());
            }
            return fileNames;
        } catch (Exception e) {
            log.error("[FILE-QUERY] Failed to list files: {}", e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to list files: " + e.getMessage());
        }
    }

    public String getPublicUrl(String filename) {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + filename;
    }

    public String getPresignedUrl(String filename) {
        return getPresignedUrl(filename, presignedUrlExpirySeconds);
    }

    public String getPresignedUrl(String filename, int expirySeconds) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucket())
                            .object(filename)
                            .expiry(expirySeconds, TimeUnit.SECONDS)
                            .build()
            );
            log.debug("[FILE-QUERY] Generated presigned URL for {} (expiry: {}s)", filename, expirySeconds);
            return url;
        } catch (Exception e) {
            log.error("[FILE-QUERY] Failed to generate presigned URL for {}: {}", filename, e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to generate presigned URL: " + e.getMessage());
        }
    }

    public List<FileItem> listFilesWithPresignedUrls() {
        return listFilesWithPresignedUrls(presignedUrlExpirySeconds);
    }

    public List<FileItem> listFilesWithPresignedUrls(int expirySeconds) {
        try {
            List<FileItem> fileItems = new ArrayList<>();
            Iterable<io.minio.Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(minioConfig.getBucket()).build()
            );
            for (io.minio.Result<Item> item : items) {
                String filename = item.get().objectName();
                String presignedUrl = getPresignedUrl(filename, expirySeconds);
                fileItems.add(FileItem.of(filename, presignedUrl));
            }
            return fileItems;
        } catch (Exception e) {
            log.error("[FILE-QUERY] Failed to list files with URLs: {}", e.getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to list files: " + e.getMessage());
        }
    }
}
