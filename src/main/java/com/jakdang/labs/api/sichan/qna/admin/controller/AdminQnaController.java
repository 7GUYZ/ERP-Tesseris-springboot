package com.jakdang.labs.api.sichan.qna.admin.controller;

import com.jakdang.labs.api.sichan.qna.dto.QnaRequestDto;
import com.jakdang.labs.api.sichan.qna.dto.QnaResponseDto;
import com.jakdang.labs.api.sichan.qna.service.AdminQnaService;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sichan/qna/admin")
@RequiredArgsConstructor
public class AdminQnaController {

    private final AdminQnaService adminQnaService;

    // QnA 목록 조회 (관리자용)
    @GetMapping("/list")
    public ResponseEntity<List<QnaResponseDto>> getQnaList(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {

        List<QnaResponseDto> qnaList = adminQnaService.getQnaList(searchType, searchKeyword);
        return ResponseEntity.ok(qnaList);
    }

    // QnA 상세 조회 (관리자용)
    @GetMapping("/{qnaIndex}")
    public ResponseEntity<QnaResponseDto> getQnaDetail(@PathVariable Integer qnaIndex) {
        QnaResponseDto qnaDetail = adminQnaService.getQnaDetail(qnaIndex);
        return ResponseEntity.ok(qnaDetail);
    }

    // QnA 답변 등록
    @PostMapping("/{qnaIndex}/answer")
    public ResponseEntity<QnaResponseDto> registerAnswer(
            @PathVariable Integer qnaIndex,
            @RequestBody QnaRequestDto requestDto,
            Authentication authentication) {

        // CustomUserDetails에서 userId 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUserId();

        QnaResponseDto response = adminQnaService.registerAnswer(qnaIndex, requestDto, userId);
        return ResponseEntity.ok(response);
    }

    // 답변 대기 중인 QnA 목록
    @GetMapping("/waiting")
    public ResponseEntity<List<QnaResponseDto>> getWaitingQnaList() {
        List<QnaResponseDto> waitingList = adminQnaService.getWaitingQnaList();
        return ResponseEntity.ok(waitingList);
    }

    // 답변 완료된 QnA 목록
    @GetMapping("/completed")
    public ResponseEntity<List<QnaResponseDto>> getCompletedQnaList() {
        List<QnaResponseDto> completedList = adminQnaService.getCompletedQnaList();
        return ResponseEntity.ok(completedList);
    }
}