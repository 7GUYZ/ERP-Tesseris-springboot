package com.jakdang.labs.api.file.controller;


import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.file.FileServiceClient;
import com.jakdang.labs.api.file.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/file")
@Slf4j
public class FileV2Controller {

    private final FileServiceClient fileServiceClient;


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody RequestFileDTO dto) {
        byte[] file = fileServiceClient.downloadFile(dto);
        return ResponseEntity.ok().body(file);
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<Object>> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam(value = "index", required = false) Integer index, @RequestParam(value = "width", required = false) Integer width, @RequestParam(value = "height", required = false) Integer height, @RequestParam(value = "ownerId", required = false) String ownerId, @RequestParam(value = "memberType", required = false) MemberEnum memberType) {

        ResponseDTO<Object> responseDTO = fileServiceClient.handleFileUpload(file, index, width, height, ownerId, memberType);

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/upload/{fileName}")
    public ResponseEntity<ResponseDTO<FileInfoDTO>> generatePresignedUrlForUpload(@PathVariable(value = "fileName") String fileName) {
        ResponseDTO<FileInfoDTO> responseDTO = fileServiceClient.generatePresignedUrlForUpload(fileName);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/upload/success")
    public ResponseEntity<ResponseDTO<ResponseFileDTO>> handleUploadSuccess(@RequestBody RequestFileDTO requestFileDTO, @RequestParam(required = false, value = "ownerId") String ownerId, @RequestParam(required = false, value = "memberType") MemberEnum memberType) {
        log.info("requestFileDTO: {}", requestFileDTO);
        ResponseDTO<ResponseFileDTO> responseDTO = fileServiceClient.handleUploadSuccess(requestFileDTO, ownerId, memberType);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(value = "/upload-audio", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<ResponseFileDTO>> uploadAudio(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "ownerId") String ownerId, @RequestParam(value = "memberType") MemberEnum memberType) {
        ResponseDTO<ResponseFileDTO> responseDTO = fileServiceClient.uploadAudio(file, ownerId, memberType);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<ResponseFileDTO>> uploadImage(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "ownerId") String ownerId, @RequestParam(value = "memberType") MemberEnum memberType) {
        ResponseDTO<ResponseFileDTO> responseDTO = fileServiceClient.uploadImage(file, ownerId, memberType);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<ResponseFileDTO>> uploadFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "ownerId") String ownerId, @RequestParam(value = "memberType") MemberEnum memberType) {
        ResponseDTO<ResponseFileDTO> responseDTO = fileServiceClient.uploadFile(file, ownerId, memberType);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/image/{fileId}")
    public ResponseEntity<byte[]> getImage(@PathVariable(value = "fileId") String fileId) {
        byte[] image = fileServiceClient.getImage(fileId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/imagelink/{fileId}")
    public ResponseEntity<ResponseDTO<String>> getImageByLink(@PathVariable(value = "fileId") String fileId)  {
        ResponseDTO<String> responseDTO = fileServiceClient.getImageLink(fileId);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/image/{fileId}/{size}")
    public ResponseEntity<byte[]> getImageResize(@PathVariable(value = "fileId") String fileId, @PathVariable(value = "size") int size) {
        byte[] image = fileServiceClient.getImageResize(fileId, size);
        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.setContentType(MediaType.IMAGE_JPEG);
            httpHeaders.setContentLength(image.length);
        }).body(image);
    }

    @PostMapping("/image/resources/{imageName}/{size}")
    public ResponseEntity<byte[]> getPublicImageResize(@PathVariable(value = "imageName") String imageName, @PathVariable(value = "size") int size) {
        if (size < 10 || size > 1000) return ResponseEntity.badRequest().body("사이즈는 최소 10부터 1000까지 가능합니다.".getBytes());

        byte[] image = fileServiceClient.getPublicImageResize(imageName, size);

        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.setContentType(MediaType.IMAGE_JPEG);
            httpHeaders.setContentLength(image.length);
        }).body(image);
    }

    @GetMapping("/thumbnail/{fileId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable(value = "fileId") String fileId) {
        byte[] image = fileServiceClient.getThumbnail(fileId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/image/member/{memberId}")
    public ResponseEntity<byte[]> getMemberImage(@PathVariable(value = "memberId") String memberId) {
        byte[] image = fileServiceClient.getMemberImage(memberId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/image/user/{userId}")
    public ResponseEntity<byte[]> getUserImage(@PathVariable(value = "userId") String userId) {
        byte[] image = fileServiceClient.getUserImage(userId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/image/school/{schoolId}")
    public ResponseEntity<byte[]> getSchoolImage(@PathVariable(value = "schoolId") String schoolId) {
        byte[] image = fileServiceClient.getSchoolImage(schoolId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/image/kid/{kidId}")
    public ResponseEntity<byte[]> getKidImage(@PathVariable(value = "kidId") String kidId) {
        byte[] image = fileServiceClient.getKidImage(kidId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @GetMapping("/image/kid_all/{kidId}")
    public ResponseEntity<List<FileOwnerDTO>> getAllKidImage(@PathVariable(value = "kidId") String kidId) {
        List<FileOwnerDTO> dto = fileServiceClient.getAllKidImage(kidId);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/image/media/{mediaId}")
    public ResponseEntity<byte[]> getImageAlbumMedia(@PathVariable(value = "mediaId") String mediaId) {
        byte[] image = fileServiceClient.getImageAlbumMedia(mediaId);
        return ResponseEntity.ok().headers(httpHeaders -> httpHeaders.setContentType(MediaType.IMAGE_JPEG)).body(image);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ResponseDTO<?>> deleteFile(@PathVariable(value = "fileId") String fileId) {
        ResponseDTO<?> dto = fileServiceClient.deleteFile(fileId);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/findAll")
    public ResponseEntity<ResponseDTO<List<ResponseFileDTO>>> findAllById(@RequestBody List<String> fileIds) throws IOException {
        ResponseDTO<List<ResponseFileDTO>> responseDTO = fileServiceClient.findAllById(fileIds);
        return ResponseEntity.ok().body(responseDTO);
    }
}

