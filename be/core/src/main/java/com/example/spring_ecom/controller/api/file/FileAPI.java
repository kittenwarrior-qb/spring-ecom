package com.example.spring_ecom.controller.api.file;

import com.example.spring_ecom.controller.api.file.model.FileListResponse;
import com.example.spring_ecom.controller.api.file.model.FileUploadResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File", description = "File management API using MinIO")
@RequestMapping("/v1/api/files")
public interface FileAPI {

    @Operation(summary = "Upload file to MinIO (returns public URL)")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ApiResponse<FileUploadResponse> uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file
    );

    @Operation(summary = "Upload file to MinIO (returns presigned URL with expiry)")
    @PostMapping(value = "/upload/presigned", consumes = "multipart/form-data")
    ApiResponse<FileUploadResponse> uploadFileWithPresignedUrl(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Expiry time in seconds (default: 3600)") 
            @RequestParam(value = "expiry", required = false) Integer expirySeconds
    );

    @Operation(summary = "Get presigned URL for existing file")
    @GetMapping("/presigned/{filename}")
    ApiResponse<String> getPresignedUrl(
            @Parameter(description = "Filename") @PathVariable String filename,
            @Parameter(description = "Expiry time in seconds (default: 3600)") 
            @RequestParam(value = "expiry", required = false) Integer expirySeconds
    );

    @Operation(summary = "List all files in bucket")
    @GetMapping("/list")
    ApiResponse<FileListResponse> listFiles();

    @Operation(summary = "Download file by filename")
    @GetMapping("/download/{filename}")
    ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Filename to download") @PathVariable String filename
    );

    @Operation(summary = "Delete file by filename")
    @DeleteMapping("/delete/{filename}")
    ApiResponse<String> deleteFile(
            @Parameter(description = "Filename to delete") @PathVariable String filename
    );
}
