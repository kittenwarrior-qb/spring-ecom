package com.example.spring_ecom.config;

import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioConfig {

    private String endpoint = "http://localhost:9000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin123";
    private String bucket = "spring-ecom";
    private int presignedUrlExpiry = 3600;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        
        setBucketPrivate(client);
        
        return client;
    }

    private void setBucketPrivate(MinioClient minioClient) {
        try {
            String privatePolicy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Deny",
                      "Principal": {
                        "AWS": ["*"]
                      },
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
            
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucket)
                            .config(privatePolicy)
                            .build()
            );
            log.info("[MINIO] Bucket '{}' set to PRIVATE", bucket);
        } catch (Exception e) {
            log.warn("[MINIO] Could not set bucket policy: {}", e.getMessage());
        }
    }
}
