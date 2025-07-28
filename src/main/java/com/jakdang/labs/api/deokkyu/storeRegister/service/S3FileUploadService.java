package com.jakdang.labs.api.deokkyu.storeRegister.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class S3FileUploadService {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private AmazonS3 amazonS3;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2) // 한국 서울 리전 고정
                .build();
    }

    /**
     * 파일을 S3에 업로드하고 URL을 반환
     * @param multipartFile 업로드할 파일
     * @param dirName 디렉토리 이름 (예: "store-images")
     * @return 업로드된 파일의 S3 URL
     */
    public String uploadFile(MultipartFile multipartFile, String dirName) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.warn("업로드할 파일이 비어있습니다.");
            return null;
        }

        try {
            // 파일명 생성 (중복 방지를 위해 UUID + 타임스탬프 사용)
            String originalFileName = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = dirName + "/" + timestamp + "_" + UUID.randomUUID().toString() + extension;

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);

            // 업로드된 파일의 URL 반환
            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            
            log.info("파일 S3 업로드 성공: originalName={}, s3FileName={}, url={}", 
                    originalFileName, fileName, fileUrl);
            
            return fileUrl;

        } catch (IOException e) {
            log.error("파일 S3 업로드 실패: {}", multipartFile.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 가맹점 신청 관련 파일 업로드 (특화 메서드)
     * @param multipartFile 업로드할 파일
     * @param fileType 파일 타입 ("business_license", "sign_photo", "front_photo")
     * @param storeId 가맹점 ID
     * @return 업로드된 파일의 S3 URL
     */
    public String uploadStoreFile(MultipartFile multipartFile, String fileType, String storeId) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String dirName = "store-applications/" + storeId + "/" + fileType;
        return uploadFile(multipartFile, dirName);
    }

    /**
     * S3에서 파일 삭제
     * @param fileUrl 삭제할 파일의 S3 URL
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                log.warn("삭제할 파일 URL이 비어있습니다.");
                return;
            }

            // URL에서 파일 키 추출
            String fileKey = extractFileKeyFromUrl(fileUrl);
            
            if (fileKey != null) {
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileKey);
                amazonS3.deleteObject(deleteObjectRequest);
                log.info("파일 S3 삭제 성공: fileKey={}", fileKey);
            }

        } catch (Exception e) {
            log.error("파일 S3 삭제 실패: url={}", fileUrl, e);
        }
    }

    /**
     * S3 URL에서 파일 키를 추출
     * @param fileUrl S3 파일 URL
     * @return 파일 키
     */
    private String extractFileKeyFromUrl(String fileUrl) {
        try {
            if (fileUrl.contains(".amazonaws.com/")) {
                return fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + 15);
            }
            return null;
        } catch (Exception e) {
            log.error("파일 키 추출 실패: url={}", fileUrl, e);
            return null;
        }
    }

    /**
     * 버킷 존재 여부 확인
     * @return 버킷 존재 여부
     */
    public boolean doesBucketExist() {
        try {
            return amazonS3.doesBucketExistV2(bucket);
        } catch (Exception e) {
            log.error("버킷 존재 여부 확인 실패: bucket={}", bucket, e);
            return false;
        }
    }
} 