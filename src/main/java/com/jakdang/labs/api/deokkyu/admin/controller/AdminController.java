package com.jakdang.labs.api.deokkyu.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListRequestDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminCreateRequestDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminDetailResponseDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminUpdateRequestDto;
import com.jakdang.labs.api.deokkyu.admin.service.AdminService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 리스트 조회 API
     * GET /api/admin/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<AdminListResponseDto>> getAdminList(
            @RequestParam(value = "adminUserEmail", required = false) String adminUserEmail,
            @RequestParam(value = "adminUserName", required = false) String adminUserName,
            @RequestParam(value = "adminUserPhone", required = false) String adminUserPhone,
            @RequestParam(value = "adminTypeName", required = false) String adminTypeName,
            @RequestParam(value = "adminRankName", required = false) String adminRankName,
            @RequestParam(value = "adminRegistrationDateStart", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate adminRegistrationDateStart,
            @RequestParam(value = "adminRegistrationDateEnd", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate adminRegistrationDateEnd) {

        log.info("관리자 리스트 조회 API 호출");

        try {
            // 요청 파라미터를 DTO로 변환
            AdminListRequestDto requestDto = AdminListRequestDto.builder()
                    .adminUserEmail(adminUserEmail)
                    .adminUserName(adminUserName)
                    .adminUserPhone(adminUserPhone)
                    .adminTypeName(adminTypeName)
                    .adminRankName(adminRankName)
                    .adminRegistrationDateStart(adminRegistrationDateStart)
                    .adminRegistrationDateEnd(adminRegistrationDateEnd)
                    .build();

            // 서비스 호출하여 관리자 리스트 조회
            List<AdminListResponseDto> adminList = adminService.getAdminList(requestDto);

            log.info("관리자 리스트 조회 완료 - 결과 개수: {}", adminList.size());
            
            return ResponseEntity.ok(adminList);

        } catch (Exception e) {
            log.error("관리자 리스트 조회 API 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 전체 관리자 리스트 조회 API
     * GET /api/admin/list/all
     */
    @GetMapping("/list/all")
    public ResponseEntity<List<AdminListResponseDto>> getAllAdminList() {
        try {
            List<AdminListResponseDto> adminList = adminService.getAllAdminList();
            return ResponseEntity.ok(adminList);
        } catch (Exception e) {
            log.error("전체 관리자 리스트 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 관리자 등록 API
     * POST /api/admin/create
     */
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody AdminCreateRequestDto createDto, @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("관리자 등록 API 호출: {}", createDto.getAdminUserEmail());
            
            // DTO 유효성 검증
            if (createDto.getAdminUserEmail() == null || createDto.getAdminUserEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다.");
            }
            if (createDto.getAdminPassword() == null || createDto.getAdminPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("비밀번호는 필수입니다.");
            }
            if (createDto.getAdminPasswordConfirm() == null || createDto.getAdminPasswordConfirm().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("비밀번호 확인은 필수입니다.");
            }
            if (!createDto.getAdminPassword().equals(createDto.getAdminPasswordConfirm())) {
                return ResponseEntity.badRequest().body("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }
            if (createDto.getAdminTypeIndex() == null) {
                return ResponseEntity.badRequest().body("관리자 타입은 필수입니다.");
            }
            
            boolean success = adminService.createAdmin(createDto, authHeader);
            if (success) {
                return ResponseEntity.ok("관리자가 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("관리자 등록에 실패했습니다. 입력 정보를 확인해주세요.");
            }
        } catch (RuntimeException e) {
            log.error("관리자 등록 중 런타임 오류", e);
            return ResponseEntity.badRequest().body("관리자 등록 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("관리자 등록 오류", e);
            return ResponseEntity.internalServerError().body("관리자 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자 상세정보 조회 API
     * GET /api/admin/detail/{userIndex}
     */
    @GetMapping("/detail/{userIndex}")
    public ResponseEntity<AdminDetailResponseDto> getAdminDetail(@PathVariable("userIndex") String userIndexStr) {
        try {
            log.info("관리자 상세정보 조회 API 호출: userIndex={}", userIndexStr);
            
            if (userIndexStr == null || userIndexStr.trim().isEmpty()) {
                log.error("userIndex가 null이거나 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }
            
            // String을 Integer로 변환
            Integer userIndex;
            try {
                userIndex = Integer.parseInt(userIndexStr.trim());
            } catch (NumberFormatException e) {
                log.error("userIndex 형식이 잘못되었습니다: {}", userIndexStr);
                return ResponseEntity.badRequest().build();
            }
            
            AdminDetailResponseDto adminDetail = adminService.getAdminDetail(userIndex);
            if (adminDetail == null) {
                log.warn("해당 userIndex의 관리자를 찾을 수 없습니다: {}", userIndex);
                return ResponseEntity.notFound().build();
            }
            
            log.info("관리자 상세정보 조회 완료: {}", adminDetail.getAdminUserEmail());
            return ResponseEntity.ok(adminDetail);
            
        } catch (Exception e) {
            log.error("관리자 상세정보 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 관리자 정보 수정 API
     * PUT /api/admin/update/{userIndex}
     */
    @PutMapping("/update/{userIndex}")
    public ResponseEntity<String> updateAdmin(@PathVariable("userIndex") String userIndexStr, 
                                            @RequestBody AdminUpdateRequestDto updateDto,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("관리자 정보 수정 API 호출: userIndex={}", userIndexStr);
            
            if (userIndexStr == null || userIndexStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("userIndex는 필수입니다.");
            }
            
            // String을 Integer로 변환
            Integer userIndex;
            try {
                userIndex = Integer.parseInt(userIndexStr.trim());
            } catch (NumberFormatException e) {
                log.error("userIndex 형식이 잘못되었습니다: {}", userIndexStr);
                return ResponseEntity.badRequest().body("userIndex 형식이 잘못되었습니다.");
            }
            
            // DTO 기본 유효성 검증
            if (updateDto.getAdminUserName() == null || updateDto.getAdminUserName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("이름은 필수입니다.");
            }
            
            boolean success = adminService.updateAdmin(userIndex, updateDto, authHeader);
            if (success) {
                return ResponseEntity.ok("관리자 정보가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("관리자 정보 수정에 실패했습니다. 입력 정보를 확인해주세요.");
            }
            
        } catch (RuntimeException e) {
            log.error("관리자 정보 수정 중 런타임 오류", e);
            return ResponseEntity.badRequest().body("관리자 정보 수정 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("관리자 정보 수정 오류", e);
            return ResponseEntity.internalServerError().body("관리자 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 