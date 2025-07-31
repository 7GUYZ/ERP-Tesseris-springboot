package com.jakdang.labs.api.deokkyu.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListRequestDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminCreateRequestDto;
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
} 