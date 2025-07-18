package com.jakdang.labs.api.jiyun.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.jiyun.notice.dto.NoticeDTO;
import com.jakdang.labs.api.jiyun.notice.service.NoticeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping("/insert")
    public ResponseEntity<?> createNotice(@RequestBody NoticeDTO.CreateRequest request) {
        boolean result = noticeService.createNotice(request);
        if (result) {
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
    public ResponseEntity<?> updateNotice(@RequestBody NoticeDTO.UpdateRequest request) {
        try {
            boolean result = noticeService.updateNotice(request);
            if (result) {
                return ResponseEntity.ok("공지사항 수정 성공");
            } else {
                return ResponseEntity.badRequest().body("공지사항 수정 실패");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 공지사항 삭제
    @PostMapping("/delete/{noticeIndex}")
    public ResponseEntity<?> deleteNotice(@PathVariable("noticeIndex") Integer noticeIndex) {
        boolean result = noticeService.deleteNotice(noticeIndex);
        if (result) {
            return ResponseEntity.ok("공지사항 삭제 성공");
        } else {
            return ResponseEntity.badRequest().body("공지사항 삭제 실패");
        }
    }
} 