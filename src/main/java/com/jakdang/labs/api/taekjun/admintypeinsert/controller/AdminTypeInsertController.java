package com.jakdang.labs.api.taekjun.admintypeinsert.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.taekjun.admintypeinsert.service.AdminTypeInsertService;
import com.jakdang.labs.api.taekjun.admintypeinsert.dto.AdminTypeInsertDTO;
import com.jakdang.labs.api.taekjun.admintypeinsert.dto.AdminTypeUpdateDTO;
import com.jakdang.labs.entity.adminType;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/admintypeinsert")
public class AdminTypeInsertController {

    private final AdminTypeInsertService adminTypeInsertService;
    private final JwtUtil jwtUtil;
    private final UserTesserisRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<adminType>> getAdminTypesOrderByOrder() {
        try {
            List<adminType> adminTypes = adminTypeInsertService.getAllAdminTypesOrderByOrder();
            return ResponseEntity.ok(adminTypes);
        } catch (Exception e) {
            log.error("AdminType 목록 조회 중 오류: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insertAdminType(
            @RequestBody AdminTypeInsertDTO insertDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String userId = jwtUtil.getUserId(token);

            UserTesseris user = userRepository.findByUsersId_Id(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "사용자 정보를 찾을 수 없습니다."
                ));
            }

            if (insertDTO.getAdminTypeName() == null || insertDTO.getAdminTypeName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "관리자 타입 이름은 필수입니다."
                ));
            }

            if (insertDTO.getInsertPosition() == null || insertDTO.getInsertPosition() < 1) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "삽입 위치는 1 이상이어야 합니다."
                ));
            }

            boolean success = adminTypeInsertService.insertAdminType(insertDTO, user.getUserIndex());

            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "관리자 타입이 성공적으로 추가되었습니다.",
                    "data", Map.of(
                        "adminTypeName", insertDTO.getAdminTypeName(),
                        "insertPosition", insertDTO.getInsertPosition()
                    )
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "관리자 타입 추가에 실패했습니다."
                ));
            }

        } catch (Exception e) {
            log.error("AdminType 삽입 중 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAdminType(
            @RequestBody AdminTypeUpdateDTO updateDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String userId = jwtUtil.getUserId(token);

            UserTesseris user = userRepository.findByUsersId_Id(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "사용자 정보를 찾을 수 없습니다."
                ));
            }

            if (updateDTO.getAdminTypeIndex() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "관리자 타입 ID는 필수입니다."
                ));
            }

            boolean success = adminTypeInsertService.updateAdminType(updateDTO, user.getUserIndex());

            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "관리자 타입이 성공적으로 수정되었습니다.",
                    "data", Map.of(
                        "adminTypeIndex", updateDTO.getAdminTypeIndex(),
                        "adminTypeName", updateDTO.getAdminTypeName(),
                        "newOrder", updateDTO.getNewOrder()
                    )
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "관리자 타입 수정에 실패했습니다."
                ));
            }

        } catch (Exception e) {
            log.error("AdminType 수정 중 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{adminTypeIndex}")
    public ResponseEntity<Map<String, Object>> deleteAdminType(
            @PathVariable Integer adminTypeIndex,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String userId = jwtUtil.getUserId(token);

            UserTesseris user = userRepository.findByUsersId_Id(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "사용자 정보를 찾을 수 없습니다."
                ));
            }

            boolean success = adminTypeInsertService.deleteAdminType(adminTypeIndex, user.getUserIndex());

            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "관리자 타입이 성공적으로 삭제되었습니다."
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "관리자 타입 삭제에 실패했습니다."
                ));
            }

        } catch (Exception e) {
            log.error("AdminType 삭제 중 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
} 