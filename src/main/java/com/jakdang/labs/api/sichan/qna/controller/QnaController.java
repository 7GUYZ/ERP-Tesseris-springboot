package com.jakdang.labs.api.sichan.qna.controller;

import com.jakdang.labs.api.sichan.qna.dto.QnaRequestDto;
import com.jakdang.labs.api.sichan.qna.dto.QnaResponseDto;
import com.jakdang.labs.api.sichan.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sichan/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // QnA 등록
    @PostMapping("/inquiry")
    public ResponseEntity<QnaResponseDto> registerInquiry(
            @RequestBody QnaRequestDto requestDto,
            Authentication authentication) {

        String userIndex = authentication.getName();
        QnaResponseDto response = qnaService.registerInquiry(requestDto, userIndex);
        return ResponseEntity.ok(response);
    }

    // QnA 내역 조회
    @GetMapping("/inquiry/list")
    public ResponseEntity<List<QnaResponseDto>> getInquiryList(
            Authentication authentication) {

        try {
            String userIndex = authentication.getName();
            if (userIndex == null || userIndex.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            List<QnaResponseDto> inquiryList = qnaService.getInquiryList(userIndex);
            return ResponseEntity.ok(inquiryList);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // QnA 상세 조회
    @GetMapping("/inquiry/{qnaIndex}")
    public ResponseEntity<QnaResponseDto> getInquiryDetail(
            @PathVariable Integer qnaIndex,
            Authentication authentication) {

        try {
            String userIndex = authentication.getName();
            if (userIndex == null || userIndex.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            QnaResponseDto inquiryDetail = qnaService.getInquiryDetail(qnaIndex, userIndex);
            return ResponseEntity.ok(inquiryDetail);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
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
}