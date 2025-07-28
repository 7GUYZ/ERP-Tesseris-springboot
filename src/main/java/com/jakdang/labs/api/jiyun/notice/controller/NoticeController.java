package com.jakdang.labs.api.jiyun.notice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.jiyun.notice.dto.NoticeDTO;
import com.jakdang.labs.api.jiyun.notice.service.NoticeService;

import java.util.List;
import com.jakdang.labs.api.jiyun.notice.dto.NoticeDTO.DeleteRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
@Slf4j // 로그 출력을 위해 추가(정은)
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping("/insert")
    public ResponseEntity<?> createNotice(@RequestBody NoticeDTO.CreateRequest request, @RequestHeader("Authorization") String authHeader) {
        boolean result = noticeService.createNotice(request, authHeader);
        if (result) {
            // 공지사항 등록 성공 시 알림 전송(정은)
            try {
                String noticeTitle = request.getNoticeTitle();
                noticeService.sendNoticeAlarm(noticeTitle);
            } catch (Exception e) {
                log.error("공지사항 등록 알림 전송 실패: {}", e.getMessage());
                // 알림 전송 실패해도 공지사항 등록은 성공으로 처리
            }
            return ResponseEntity.ok("공지사항 등록 성공");
        } else {
            return ResponseEntity.badRequest().body("공지사항 등록 실패");
        }
    }

    // 공지사항 상세
    @GetMapping("/detail/{noticeIndex}")
    public ResponseEntity<NoticeDTO.Response> getNotice(@PathVariable("noticeIndex") Integer noticeIndex) {
        try {
            NoticeDTO.Response notice = noticeService.getNotice(noticeIndex);
            return ResponseEntity.ok(notice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 공지사항 목록
    @GetMapping("/list")
    public ResponseEntity<List<NoticeDTO.Response>> getNoticeList() {
        List<NoticeDTO.Response> notices = noticeService.getNoticeList();
        return ResponseEntity.ok(notices);
    }

    // 공지사항 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateNotice(@RequestBody NoticeDTO.UpdateRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            boolean result = noticeService.updateNotice(request, authHeader);
            if (result) {
                return ResponseEntity.ok("공지사항 수정 성공");
            } else {
                return ResponseEntity.status(403).body("비밀번호가 일치하지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 공지사항 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteNotice(@RequestBody DeleteRequest request, @RequestHeader("Authorization") String authHeader) {
        boolean result = noticeService.deleteNotice(request, authHeader);
        if (result) {
            return ResponseEntity.ok("공지사항 삭제 성공");
        } else {
            return ResponseEntity.status(403).body("비밀번호가 일치하지 않습니다.");
        }
    }
} 