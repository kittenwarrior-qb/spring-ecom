package com.example.spring_ecom.controller.api.upload;

import com.example.spring_ecom.controller.api.upload.model.UploadResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Upload", description = "Upload API")
@RequestMapping("v1/api/upload")
public interface UploadAPI {

    @Operation(summary = "Upload image to Cloudinary")
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    ApiResponse<UploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    );
}
