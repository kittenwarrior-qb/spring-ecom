package com.example.spring_ecom.controller.api.upload;

import com.example.spring_ecom.controller.api.upload.model.UploadResponse;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.upload.UploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UploadController implements UploadAPI {

    private final UploadUseCase uploadUseCase;

    @Override
    @RateLimit(type = RateLimitType.USER, limit = 100, duration = 1, unit = ChronoUnit.MINUTES,
               message = "Too many file upload attempts. Please try again later.")
    public ApiResponse<UploadResponse> uploadImage(MultipartFile file, Authentication authentication) {
        Map<String, Object> uploadResult = uploadUseCase.uploadImage(file);

        UploadResponse response = new UploadResponse(
            (String) uploadResult.get("secure_url"),
            (String) uploadResult.get("public_id"),
            (String) uploadResult.get("format"),
            ((Number) uploadResult.get("bytes")).longValue(),
            (Integer) uploadResult.get("width"),
            (Integer) uploadResult.get("height")
        );

        return ApiResponse.Success.of(ResponseCode.CREATED, "Image uploaded successfully", response);
    }
}
