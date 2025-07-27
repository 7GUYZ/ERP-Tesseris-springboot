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
import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import lombok.RequiredArgsConstructor;
import com.jakdang.labs.api.alarm.service.AlarmSvc; // 알림 송신 위해 추가
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo; // 알림 송신 위해 추가

@Service
@RequiredArgsConstructor
public class NoticeService {
  private final NoticekjyRepository noticeRepository;
  private final NoticeUserkjyRepository userRepository;
  private final JwtUtil jwtUtil;
  private final AuthRepository authRepository;
  private final Argon2PasswordEncoder passwordEncoder;
  private final AlarmSvc alarmSvc; // 알림 송신 위해 추가
  private final UserTesserisLjeRepo userRepo; // 알림 송신 위해 추가

  // 공지사항 등록
  @Transactional
  public boolean createNotice(NoticeDTO.CreateRequest request, String authHeader) {
    try {
      String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
      String userId = jwtUtil.getUserId(token); // 토큰에서 userId 추출

      // userId로 UserTesseris 조회
      UserTesseris user = userRepository.findByUsersId_Id(userId).orElse(null);
      if (user == null) return false;

      Notice notice = new Notice();
      notice.setUserIndex(user.getUserIndex()); // userIndex 저장
      notice.setNoticeTitle(request.getNoticeTitle());
      notice.setNoticeDesc(request.getNoticeDesc());
      notice.setNoticeCreateTime(LocalDateTime.now());
      noticeRepository.save(notice);
      
      // 공지사항 등록 성공 시 알림 전송 (알림 송신 위해 추가)
      try {
        sendNoticeAlarm(request.getNoticeTitle());
      } catch (Exception e) {
        // 알림 전송 실패해도 공지사항 등록은 성공으로 처리
        System.err.println("공지사항 알림 전송 실패: " + e.getMessage());
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
  public boolean updateNotice(NoticeDTO.UpdateRequest request, String authHeader) {
    try {
      if (!verifyPassword(request.getPassword(), authHeader)) return false;
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
  public boolean deleteNotice(DeleteRequest request, String authHeader) {
    try {
      if (!verifyPassword(request.getPassword(), authHeader)) return false;
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
    if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
    String token = authHeader.substring(7);
    String email = jwtUtil.getUserEmail(token);
    UserEntity user = authRepository.findByEmail(email).orElse(null);
    if (user == null) return false;
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
    dto.setNoticeCreateTime(notice.getNoticeCreateTime());
    return dto;
  }

  /**
   * 공지사항 알림 전송
   */
  private void sendNoticeAlarm(String noticeTitle) {
    try {
      // 모든 사용자 목록 조회
      List<String> allUserIndexes = new ArrayList<>();
      allUserIndexes.addAll(userRepo.findUserIndexesByRole(1)); // 일반(정회원)
      allUserIndexes.addAll(userRepo.findUserIndexesByRole(2)); // 사업자
      allUserIndexes.addAll(userRepo.findUserIndexesByRole(3)); // 가맹점

      // 공지사항 알림 타입 ID (실제 DB의 alarmTypes 테이블에서 확인 필요)
      Integer noticeAlarmTypeId = 4; // TODO: 실제 알림 타입 ID로 변경

      // 알림 전송
      alarmSvc.sendAlarmWithValue(noticeAlarmTypeId, allUserIndexes, new ArrayList<>(), noticeTitle);
      
    } catch (Exception e) {
      System.err.println("공지사항 알림 전송 중 오류: " + e.getMessage());
    }
  }
} 