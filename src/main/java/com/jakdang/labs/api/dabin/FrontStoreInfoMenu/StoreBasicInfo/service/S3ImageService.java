package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ImageService {
    
    @Autowired
    private AmazonS3 amazonS3;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    
    @Value("${cloud.aws.s3.region.static}")
    private String region;
    
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        
        return fileName; // S3 Key만 반환
    }

    public String generatePresignedUrl(String fileKey) {
        java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 10); // 10분
        com.amazonaws.services.s3.model.GeneratePresignedUrlRequest request =
            new com.amazonaws.services.s3.model.GeneratePresignedUrlRequest(bucketName, fileKey)
                .withMethod(com.amazonaws.HttpMethod.GET)
                .withExpiration(expiration);
        java.net.URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
    }
    
    public void deleteImage(String imageUrl) {
        System.out.println("[S3ImageService] deleteImage 호출, imageUrl: " + imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                amazonS3.deleteObject(bucketName, imageUrl);
                System.out.println("[S3ImageService] S3에서 삭제 명령 실행: " + bucketName + "/" + imageUrl);
            } catch (Exception e) {
                System.out.println("[S3ImageService] S3 삭제 실패: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[S3ImageService] S3 삭제 스킵: imageUrl이 null 또는 빈값");
        }
    }
} 