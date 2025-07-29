package com.jakdang.labs.api.sichan.qna.controller;

import com.jakdang.labs.api.sichan.qna.dto.QnaRequestDto;
import com.jakdang.labs.api.sichan.qna.dto.QnaResponseDto;
import com.jakdang.labs.api.sichan.qna.service.QnaService;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sichan/qna")
@RequiredArgsConstructor
@Slf4j
public class QnaController {

    private final QnaService qnaService;
    private final UserTesserisRepository userTesserisRepository;

    // QnA 등록
    @PostMapping("/inquiry")
    public ResponseEntity<QnaResponseDto> registerInquiry(
            @RequestBody QnaRequestDto requestDto,
            Authentication authentication) {

        try {
            if (authentication == null) {
                log.error("인증 정보가 null입니다.");
                return ResponseEntity.status(401).build();
            }

            // CustomUserDetails에서 사용자 ID 추출
            String userId = extractUserId(authentication);
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                    .orElse(null);

            if (userTesseris == null) {
                log.warn("존재하지 않는 사용자 ID: {}", userId);
                return ResponseEntity.status(404).build();
            }

            Integer userIndex = userTesseris.getUserIndex();
            QnaResponseDto response = qnaService.registerInquiry(requestDto, userIndex.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("QnA 등록 중 오류 발생", e);
            return ResponseEntity.status(500).build();
        }
    }

    // QnA 내역 조회
    @GetMapping("/inquiry/list")
    public ResponseEntity<List<QnaResponseDto>> getInquiryList(
            Authentication authentication) {

        try {
            log.info("QnA 내역 조회 요청 시작");

            if (authentication == null) {
                log.error("인증 정보가 null입니다.");
                return ResponseEntity.status(401).build();
            }

            // CustomUserDetails에서 사용자 ID 추출
            String userId = extractUserId(authentication);
            log.info("인증된 사용자 ID (userId): {}", userId);

            if (userId == null || userId.isEmpty()) {
                log.warn("사용자 ID가 null이거나 비어있음");
                return ResponseEntity.status(401).build();
            }

            // userId로 UserTesseris 조회
            log.info("UserTesseris 조회 시도 - userId: {}", userId);

            UserTesseris userTesseris = null;
            try {
                // 먼저 UserEntity가 존재하는지 확인
                log.info("UserEntity 존재 여부 확인 중...");

                // 방법 1: Spring Data JPA 메서드
                userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                        .orElse(null);

                if (userTesseris == null) {
                    log.warn("Spring Data JPA 메서드로 조회 실패 - userId: {}", userId);

                    // 방법 2: 직접 SQL 쿼리
                    log.info("직접 SQL 쿼리로 재시도...");
                    userTesseris = userTesserisRepository.findByUserIdDirect(userId)
                            .orElse(null);

                    if (userTesseris == null) {
                        log.warn("직접 SQL 쿼리로도 조회 실패 - userId: {}", userId);

                        // 전체 UserTesseris 목록을 로그로 확인 (디버깅용)
                        long totalUsers = userTesserisRepository.count();
                        log.info("전체 UserTesseris 수: {}", totalUsers);

                        // 모든 UserTesseris의 users_id를 로그로 확인
                        if (totalUsers > 0) {
                            log.info("=== 모든 UserTesseris의 users_id ===");
                            userTesserisRepository.findAll().forEach(user -> {
                                String userUserId = user.getUsersId() != null ? user.getUsersId().getId() : "null";
                                log.info("UserTesseris[{}] - users_id: {}", user.getUserIndex(), userUserId);
                            });
                            log.info("찾으려는 userId: {}", userId);
                        }

                        return ResponseEntity.status(404).build();
                    } else {
                        log.info("직접 SQL 쿼리로 조회 성공 - userIndex: {}", userTesseris.getUserIndex());
                    }
                } else {
                    log.info("Spring Data JPA 메서드로 조회 성공 - userIndex: {}", userTesseris.getUserIndex());
                }

                log.info("UserTesseris 조회 성공 - userIndex: {}", userTesseris.getUserIndex());
            } catch (Exception e) {
                log.error("UserTesseris 조회 중 예외 발생: {}", e.getMessage(), e);
                return ResponseEntity.status(500).build();
            }

            Integer userIndex = userTesseris.getUserIndex();
            log.info("사용자 확인 완료 - userIndex: {}", userIndex);

            List<QnaResponseDto> inquiryList = qnaService.getInquiryList(userIndex.toString());
            log.info("QnA 내역 조회 완료 - 개수: {}", inquiryList.size());

            return ResponseEntity.ok(inquiryList);
        } catch (RuntimeException e) {
            log.error("QnA 내역 조회 중 런타임 오류 발생", e);
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            log.error("QnA 내역 조회 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(500).build();
        }
    }

    // QnA 상세 조회
    @GetMapping("/inquiry/{qnaIndex}")
    public ResponseEntity<QnaResponseDto> getInquiryDetail(
            @PathVariable Integer qnaIndex,
            Authentication authentication) {

        try {
            if (authentication == null) {
                log.error("인증 정보가 null입니다.");
                return ResponseEntity.status(401).build();
            }

            // CustomUserDetails에서 사용자 ID 추출
            String userId = extractUserId(authentication);
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                    .orElse(null);

            if (userTesseris == null) {
                log.warn("존재하지 않는 사용자 ID: {}", userId);
                return ResponseEntity.status(404).build();
            }

            Integer userIndex = userTesseris.getUserIndex();
            QnaResponseDto inquiryDetail = qnaService.getInquiryDetail(qnaIndex, userIndex.toString());
            return ResponseEntity.ok(inquiryDetail);
        } catch (Exception e) {
            log.error("QnA 상세 조회 중 오류 발생 - qnaIndex: {}", qnaIndex, e);
            return ResponseEntity.status(500).build();
        }
    }

    // CustomUserDetails에서 사용자 ID 추출하는 헬퍼 메서드
    private String extractUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        } else {
            // fallback: 기존 방식 사용
            return authentication.getName();
        }
    }

    // 데이터베이스 연결 테스트
    @GetMapping("/db-test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            long userCount = userTesserisRepository.count();
            response.put("status", "SUCCESS");
            response.put("userCount", userCount);
            response.put("message", "데이터베이스 연결 성공");
            log.info("데이터베이스 테스트 성공 - 사용자 수: {}", userCount);

            // 첫 번째 사용자 정보도 함께 반환 (디버깅용)
            if (userCount > 0) {
                UserTesseris firstUser = userTesserisRepository.findAll().get(0);
                response.put("firstUserIndex", firstUser.getUserIndex());
                response.put("firstUserId", firstUser.getUsersId() != null ? firstUser.getUsersId().getId() : "null");
                log.info("첫 번째 사용자 - userIndex: {}, userId: {}",
                        firstUser.getUserIndex(),
                        firstUser.getUsersId() != null ? firstUser.getUsersId().getId() : "null");
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 연결 실패: " + e.getMessage());
            log.error("데이터베이스 테스트 실패", e);
        }
        return ResponseEntity.ok(response);
    }

    // 테스트용 엔드포인트
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("QnA API is working!");
    }

    // 헬스체크용 엔드포인트
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "QnA API is running");
        return ResponseEntity.ok(response);
    }

    // 현재 로그인한 사용자 정보 확인용 엔드포인트
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null) {
                response.put("error", "인증 정보가 null입니다.");
                return ResponseEntity.ok(response);
            }

            // CustomUserDetails에서 사용자 ID 추출
            String userId = extractUserId(authentication);
            response.put("jwt_userId", userId);

            // UserTesseris 조회 시도
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                    .orElse(null);

            if (userTesseris != null) {
                response.put("userTesseris_found", true);
                response.put("userIndex", userTesseris.getUserIndex());
                response.put("userTesseris_usersId",
                        userTesseris.getUsersId() != null ? userTesseris.getUsersId().getId() : "null");
            } else {
                response.put("userTesseris_found", false);

                // 직접 SQL 쿼리로 재시도
                UserTesseris directUser = userTesserisRepository.findByUserIdDirect(userId)
                        .orElse(null);

                if (directUser != null) {
                    response.put("direct_query_found", true);
                    response.put("direct_userIndex", directUser.getUserIndex());
                } else {
                    response.put("direct_query_found", false);
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}