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
        
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }
    
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && imageUrl.contains(bucketName)) {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            amazonS3.deleteObject(bucketName, key);
        }
    }
} 