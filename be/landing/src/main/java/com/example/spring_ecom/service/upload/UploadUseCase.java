package com.example.spring_ecom.service.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UploadUseCase {
    
    Map<String, Object> uploadImage(MultipartFile file);
    
    void deleteImage(String publicId);
}
