package com.example.spring_ecom.controller.api.file;

import com.example.spring_ecom.controller.api.file.model.FileListResponse;
import com.example.spring_ecom.controller.api.file.model.FileUploadResponse;
import com.example.spring_ecom.controller.api.file.model.UploadFileRequest;
import com.example.spring_ecom.controller.api.file.model.UploadFileRequestMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.file.FileUseCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Controller
 * Thin controller - delegates all logic to UseCase
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController implements FileAPI {

    private final FileUseCaseService fileUseCaseService;
    private final UploadFileRequestMapper uploadFileRequestMapper;

    // ========== WRITE OPERATIONS ==========

    @Override
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('FILE_UPLOAD')")
    public ApiResponse<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("[FILE-CTRL] Upload request: {}", file.getOriginalFilename());
        try {
            UploadFileRequest request = uploadFileRequestMapper.toRequest(file);
            FileUploadResponse response = fileUseCaseService.uploadWithPublicUrl(request);
            return ApiResponse.Success.of(ResponseCode.CREATED, "File uploaded successfully", response);
        } catch (Exception e) {
            log.error("[FILE-CTRL] Failed to upload file: {}", e.getMessage());
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    @PostMapping(value = "/upload/presigned", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('FILE_UPLOAD')")
    public ApiResponse<FileUploadResponse> uploadFileWithPresignedUrl(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "expiry", required = false) Integer expirySeconds) {
        log.info("[FILE-CTRL] Upload with presigned URL request: {}", file.getOriginalFilename());
        try {
            UploadFileRequest request = uploadFileRequestMapper.toRequest(file);
            FileUploadResponse response;
            if (expirySeconds != null) {
                response = fileUseCaseService.uploadWithPresignedUrl(request, expirySeconds);
            } else {
                response = fileUseCaseService.uploadWithPresignedUrl(request);
            }
            return ApiResponse.Success.of(ResponseCode.CREATED, "File uploaded with presigned URL", response);
        } catch (Exception e) {
            log.error("[FILE-CTRL] Failed to upload file: {}", e.getMessage());
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/delete/{filename}")
    @PreAuthorize("hasAuthority('FILE_DELETE')")
    public ApiResponse<String> deleteFile(@PathVariable String filename) {
        log.info("[FILE-CTRL] Delete request: {}", filename);
        fileUseCaseService.deleteFile(filename);
        return ApiResponse.Success.of(ResponseCode.OK, "File deleted successfully", filename);
    }

    // ========== READ OPERATIONS ==========

    @Override
    @GetMapping("/presigned/{filename}")
    public ApiResponse<String> getPresignedUrl(
            @PathVariable String filename,
            @RequestParam(value = "expiry", required = false) Integer expirySeconds) {
        log.info("[FILE-CTRL] Get presigned URL request: {}", filename);
        String presignedUrl = fileUseCaseService.getPresignedUrlResponse(filename, expirySeconds);
        return ApiResponse.Success.of(ResponseCode.OK, "Presigned URL generated", presignedUrl);
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<FileListResponse> listFiles() {
        log.info("[FILE-CTRL] List files request");
        FileListResponse response = fileUseCaseService.listFilesResponse();
        return ApiResponse.Success.of(ResponseCode.OK, "Files retrieved successfully", response);
    }

    @Override
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        log.info("[FILE-CTRL] Download request: {}", filename);
        Resource resource = fileUseCaseService.downloadFileResource(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
