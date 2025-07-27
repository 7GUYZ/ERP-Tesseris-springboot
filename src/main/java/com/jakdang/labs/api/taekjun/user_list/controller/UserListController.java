package com.jakdang.labs.api.taekjun.user_list.controller;

import com.jakdang.labs.api.taekjun.user_list.Dto.UserListResponseDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserListSearchDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserUpdateRequestDTO;
import com.jakdang.labs.api.taekjun.user_list.service.UserListService;
import com.jakdang.labs.api.taekjun.address.service.KakaoAddressService;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jihun.common.config.ExcelDownloadConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user-admin-list")
@RequiredArgsConstructor
public class UserListController {
    private final UserListService userListService;
    private final KakaoAddressService kakaoAddressService;
    private final ExcelDownloadConfig.ExcelDownloadProperties excelDownloadProperties;

    @GetMapping
    public ResponseEntity<List<UserListResponseDTO>> getUserList() {
        try {
            System.out.println("회원 목록 API 호출됨");
            List<UserListResponseDTO> list = userListService.getUserList();
            System.out.println("반환할 데이터 개수: " + list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            System.err.println("회원 목록 API 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<UserListResponseDTO>> searchUserList(@RequestBody UserListSearchDTO searchDTO) {
        List<UserListResponseDTO> list = userListService.getUserListWithSearch(searchDTO);
        return ResponseEntity.ok(list);
    }
    
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadUserList(@RequestBody UserListSearchDTO searchDTO) {
        try {
            byte[] csvData = userListService.generateCsvFile(searchDTO);
            
            // 파일명 생성 (현재 날짜 포함)
            String fileName = "회원목록_" + java.time.LocalDate.now().toString().replace("-", "") + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/update/{userIndex}")
    public ResponseEntity<String> updateUser(@PathVariable Integer userIndex,
                                             @RequestBody UserUpdateRequestDTO updateDTO) {
        try {
            boolean success = userListService.updateUser(userIndex, updateDTO);
            if (success) {
                return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("회원 정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("회원 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 주소 검색 API (카카오 API 사용)
     */
    @GetMapping("/search-address")
    public ResponseDTO<?> searchAddress(@RequestParam String query) {
        try {
            Map<String, Object> result = kakaoAddressService.searchAddress(query);
            
            if (result != null) {
                return ResponseDTO.createSuccessResponse("주소 검색 완료", result);
            } else {
                return ResponseDTO.createErrorResponse(404, "주소를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            return ResponseDTO.createErrorResponse(500, "주소 검색 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 키워드 검색 API (상세 주소 검색용)
     */
    @GetMapping("/search-address/keyword")
    public ResponseDTO<?> searchAddressKeyword(@RequestParam String query) {
        try {
            Map<String, Object> result = kakaoAddressService.searchKeyword(query);
            
            if (result != null) {
                return ResponseDTO.createSuccessResponse("키워드 검색 완료", result);
            } else {
                return ResponseDTO.createErrorResponse(404, "검색 결과를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            return ResponseDTO.createErrorResponse(500, "키워드 검색 중 오류가 발생했습니다.");
        }
    }
} 