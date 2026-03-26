package com.example.spring_ecom.service.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadUseCaseService implements UploadUseCase {

    private final CloudinaryService cloudinaryService;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) {
        log.info("Uploading image: {}", file.getOriginalFilename());
        Map<String, Object> result = cloudinaryService.uploadImage(file);
        log.info("Image uploaded successfully: {}", result.get("secure_url"));
        return result;
    }

    @Override
    public void deleteImage(String publicId) {
        log.info("Deleting image with publicId: {}", publicId);
        cloudinaryService.deleteImage(publicId);
        log.info("Image deleted successfully");
    }
}
