package com.jakdang.labs.api.notice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.notice.dto.NoticeDTO;
import com.jakdang.labs.api.notice.entity.Notice;
import com.jakdang.labs.api.notice.entity.User;
import com.jakdang.labs.api.notice.repository.NoticeRepository;
import com.jakdang.labs.api.notice.repository.NoticeUserRepository;

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
    return noticeRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
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
    if (notice.getUserIndex() != null) {
      userId = userRepository.findByUserIndex(notice.getUserIndex())
        .map(User::getUserId)
        .orElse(null);
    }
    dto.setUserId(userId);
    
    dto.setNoticeTitle(notice.getNoticeTitle());
    dto.setNoticeDesc(notice.getNoticeDesc());
    dto.setNoticeCreateTime(notice.getNoticeCreateTime());
    return dto;
  }
}
