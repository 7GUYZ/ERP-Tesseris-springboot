package com.jakdang.labs.api.jiyun.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.jiyun.dto.NoticeDTO;
import com.jakdang.labs.api.entity.Notice;
import com.jakdang.labs.api.entity.UserTesseris;
import com.jakdang.labs.api.jiyun.repository.NoticeRepository;
import com.jakdang.labs.api.jiyun.repository.NoticeUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
  private final NoticeRepository noticeRepository;
  private final NoticeUserRepository userRepository;

  // 공지사항 등록
  @Transactional
  public boolean createNotice(NoticeDTO.CreateRequest request) {
    try {
      Notice notice = new Notice();
      // userIndex를 직접 설정
      notice.setUserIndex(request.getUserIndex());
      notice.setNoticeTitle(request.getNoticeTitle());
      notice.setNoticeDesc(request.getNoticeDesc());
      notice.setNoticeCreateTime(LocalDateTime.now());
      noticeRepository.save(notice);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 공지사항 상세
  @Transactional(readOnly = true)
  public NoticeDTO.Response getNotice(Integer noticeIndex) {
    Notice notice = noticeRepository.findById(noticeIndex)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
    return toResponse(notice);
  }

  // 공지사항 목록
  @Transactional(readOnly = true)
  public List<NoticeDTO.Response> getNoticeList() {
    try {
      List<Notice> notices = noticeRepository.findAll();
      System.out.println("조회된 공지사항 수: " + notices.size());
      return notices.stream()
          .map(this::toResponse)
          .collect(Collectors.toList());
    } catch (Exception e) {
      System.out.println("공지사항 목록 조회 중 오류: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // 공지사항 수정
  @Transactional
  public boolean updateNotice(NoticeDTO.UpdateRequest request) {
    try {
      Notice notice = noticeRepository.findById(request.getNoticeIndex())
          .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
      notice.setNoticeTitle(request.getNoticeTitle());
      notice.setNoticeDesc(request.getNoticeDesc());
      noticeRepository.save(notice);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 공지사항 삭제
  @Transactional
  public boolean deleteNotice(Integer noticeIndex) {
    try {
      Notice notice = noticeRepository.findById(noticeIndex)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
      noticeRepository.delete(notice);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

      // Entity → DTO 변환
  private NoticeDTO.Response toResponse(Notice notice) {
    NoticeDTO.Response dto = new NoticeDTO.Response();
    dto.setNoticeIndex(notice.getNoticeIndex());
    
    // userIndex → userId 변환
    String userId = null;
    try {
      if (notice.getUserIndex() != null) {
        // userIndex로 UserTesseris를 찾아서 email 가져오기
        UserTesseris user = userRepository.findByUserIndex(notice.getUserIndex())
            .orElse(null);
        if (user != null && user.getUsersId() != null) {
          userId = user.getUsersId().getId();
        }
      }
    } catch (Exception e) {
      System.out.println("User 정보 조회 중 오류: " + e.getMessage());
      userId = "unknown";
    }
    
    dto.setUserId(userId);
    dto.setNoticeTitle(notice.getNoticeTitle());
    dto.setNoticeDesc(notice.getNoticeDesc());
    dto.setNoticeCreateTime(notice.getNoticeCreateTime());
    return dto;
  }
} 