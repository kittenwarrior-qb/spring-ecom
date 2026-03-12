package com.example.spring_ecom.service.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, Object> uploadImage(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "File is empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (Objects.isNull(contentType) || !contentType.startsWith("image/")) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "File must be an image");
            }

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "spring-ecom",
                    "resource_type", "image"
                )
            );

            return uploadResult;
        } catch (IOException e) {
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to upload image: " + e.getMessage());
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete image: " + e.getMessage());
        }
    }
}
