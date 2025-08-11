package com.jakdang.labs.api.jiyun.notice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.entity.Notice;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.jiyun.notice.dto.NoticeDTO;
import com.jakdang.labs.api.jiyun.notice.repository.NoticeUserkjyRepository;
import com.jakdang.labs.api.jiyun.notice.repository.NoticekjyRepository;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.api.jiyun.notice.dto.NoticeDTO.DeleteRequest;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 출력을 위해 추가(정은)
public class NoticeService {
  private final NoticekjyRepository noticeRepository;
  private final NoticeUserkjyRepository userRepository;
  private final JwtUtil jwtUtil;
  private final AuthRepository authRepository;
  private final Argon2PasswordEncoder passwordEncoder;
  private final AlarmSvc alarmSvc; // 알림 송신 위해 추가(정은)

  // 공지사항 등록
  @Transactional
  public boolean createNotice(NoticeDTO.CreateRequest request, String authHeader) {
    try {
      String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
      String userId = jwtUtil.getUserId(token); // 토큰에서 userId 추출

      // userId로 UserTesseris 조회
      UserTesseris user = userRepository.findByUsersId_Id(userId).orElse(null);
      if (user == null)
        return false;

      Notice notice = new Notice();
      notice.setUserIndex(user.getUserIndex()); // userIndex 저장
      notice.setNoticeTitle(request.getNoticeTitle());
      notice.setNoticeDesc(request.getNoticeDesc());
      
      // noticeType 유효성 검사 및 설정
      String noticeType = request.getNoticeType();
      if (noticeType != null && (noticeType.equals("중요") || noticeType.equals("일반"))) {
        notice.setNoticeType(noticeType);
      } else {
        notice.setNoticeType("일반"); // 기본값
      }
      
      notice.setNoticeCreateTime(LocalDateTime.now());
      noticeRepository.save(notice);

      // 공지사항 등록 성공 시 알림 전송(정은)
      try {
        String noticeTitle = request.getNoticeTitle();
        alarmSvc.sendNoticeAlarm(user.getUserIndex(), noticeTitle);
        log.info("공지사항 알림 전송 완료: {}", noticeTitle);
      } catch (Exception e) {
        log.error("공지사항 등록 알림 전송 실패: {}", e.getMessage());
        // 알림 전송 실패해도 공지사항 등록은 성공으로 처리
      }
      
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

  // 공지사항 목록 (중요공지 우선)
  @Transactional(readOnly = true)
  public List<NoticeDTO.Response> getNoticeList() {
    try {
      List<Notice> notices = noticeRepository.findAll();
      System.out.println("조회된 공지사항 수: " + notices.size());
      
      // 모든 공지사항을 최신순으로 정렬
      List<NoticeDTO.Response> allNotices = notices.stream()
          .map(this::toResponse)
          .sorted((a, b) -> b.getNoticeCreateTime().compareTo(a.getNoticeCreateTime()))
          .collect(Collectors.toList());
      
      return allNotices;
    } catch (Exception e) {
      System.out.println("공지사항 목록 조회 중 오류: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // 공지사항 수정
  @Transactional
  public boolean updateNotice(NoticeDTO.UpdateRequest request, String authHeader) {
    try {
      if (!verifyPassword(request.getPassword(), authHeader))
        return false;
      Notice notice = noticeRepository.findById(request.getNoticeIndex())
          .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
      notice.setNoticeTitle(request.getNoticeTitle());
      notice.setNoticeDesc(request.getNoticeDesc());
      
      // noticeType 유효성 검사 및 설정
      String noticeType = request.getNoticeType();
      if (noticeType != null && (noticeType.equals("중요") || noticeType.equals("일반"))) {
        notice.setNoticeType(noticeType);
      } else {
        notice.setNoticeType("일반"); // 기본값
      }
      
      noticeRepository.save(notice);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 공지사항 삭제
  @Transactional
  public boolean deleteNotice(DeleteRequest request, String authHeader) {
    try {
      if (!verifyPassword(request.getPassword(), authHeader))
        return false;
      Notice notice = noticeRepository.findById(request.getNoticeIndex())
          .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
      noticeRepository.delete(notice);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 비밀번호 검증
  public boolean verifyPassword(String password, String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      return false;
    String token = authHeader.substring(7);
    String email = jwtUtil.getUserEmail(token);
    UserEntity user = authRepository.findByEmail(email).orElse(null);
    if (user == null)
      return false;
    return passwordEncoder.matches(password, user.getPassword());
  }

  // Entity → DTO 변환
  private NoticeDTO.Response toResponse(Notice notice) {
    NoticeDTO.Response dto = new NoticeDTO.Response();
    dto.setNoticeIndex(notice.getNoticeIndex());

    // userIndex → userEmail 변환
    String userEmail = null;
    try {
      if (notice.getUserIndex() != null) {
        // userIndex로 UserTesseris를 찾아서 email 가져오기
        UserTesseris user = userRepository.findByUserIndex(notice.getUserIndex())
            .orElse(null);
        if (user != null && user.getUsersId() != null) {
          userEmail = user.getUsersId().getEmail();
        }
      }
    } catch (Exception e) {
      System.out.println("User 정보 조회 중 오류: " + e.getMessage());
      userEmail = "unknown";
    }

    dto.setUserEmail(userEmail);
    dto.setNoticeTitle(notice.getNoticeTitle());
    dto.setNoticeDesc(notice.getNoticeDesc());
    dto.setNoticeType(notice.getNoticeType());
    dto.setNoticeCreateTime(notice.getNoticeCreateTime());
    return dto;
  }

}