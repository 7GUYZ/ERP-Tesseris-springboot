package com.jakdang.labs.api.sichan.qna.service;

import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.sichan.qna.dto.QnaRequestDto;
import com.jakdang.labs.api.sichan.qna.dto.QnaResponseDto;
import com.jakdang.labs.api.sichan.qna.repository.QnaRepository;
import com.jakdang.labs.entity.Qna;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserTesserisRepository userTesserisRepository;
    private final AlarmSvc alarmSvc;

    // QnA 등록
    @Transactional
    public QnaResponseDto registerInquiry(QnaRequestDto requestDto, String userIndex) {
        UserTesseris user = userTesserisRepository.findByUserIndex(Integer.parseInt(userIndex))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Qna qna = new Qna();
        qna.setQuestionUser(user);
        qna.setQuestionTitle(requestDto.getQuestionTitle());
        qna.setQuestionDesc(requestDto.getQuestionDesc());
        qna.setQnaCreateTime(LocalDateTime.now());

        Qna savedQna = qnaRepository.save(qna);

        // 신규 Q&A 등록 알림 서비스
        try {
            alarmSvc.sendQnaRegisterAlarm(Integer.parseInt(userIndex));
            log.info("신규 Q&A 등록 알림 전송 완료");
        } catch (Exception e) {
            log.error("신규 Q&A 등록 알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패해도 Q&A DB 저장은 성공으로 처리
        }
        return convertToResponseDto(savedQna);
    }

    // QnA 내역 조회
    @Transactional(readOnly = true)
    public List<QnaResponseDto> getInquiryList(String userIndex) {
        try {
            log.info("QnA 목록 조회 시작 - userIndex: {}", userIndex);

            if (userIndex == null || userIndex.trim().isEmpty()) {
                log.error("사용자 ID가 null이거나 비어있음");
                throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
            }

            Integer userIndexInt;
            try {
                userIndexInt = Integer.parseInt(userIndex);
            } catch (NumberFormatException e) {
                log.error("유효하지 않은 사용자 ID 형식: {}", userIndex, e);
                throw new RuntimeException("유효하지 않은 사용자 ID 형식입니다.");
            }

            // 사용자 존재 여부 확인
            UserTesseris user = userTesserisRepository.findByUserIndex(userIndexInt)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIndex));

            log.info("사용자 확인 완료: {}", user.getUserIndex());

            // QnA 목록 조회
            List<Qna> qnaList = qnaRepository.findByQuestionUserIndex(userIndexInt);

            log.info("QnA 목록 조회 완료 - 개수: {}", qnaList.size());

            return qnaList.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            log.error("QnA 목록 조회 중 런타임 오류 발생 - userIndex: {}", userIndex, e);
            throw e;
        } catch (Exception e) {
            log.error("QnA 목록 조회 중 예상치 못한 오류 발생 - userIndex: {}", userIndex, e);
            throw new RuntimeException("QnA 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // QnA 상세 조회
    @Transactional(readOnly = true)
    public QnaResponseDto getInquiryDetail(Integer qnaIndex, String userIndex) {
        Qna qna = qnaRepository.findByQnaIndexAndQuestionUserIndex(qnaIndex, Integer.parseInt(userIndex))
                .orElseThrow(() -> new RuntimeException("QnA를 찾을 수 없습니다."));
        return convertToResponseDto(qna);
    }

    // Entity를 ResponseDto로 변환
    private QnaResponseDto convertToResponseDto(Qna qna) {
        try {
            QnaResponseDto responseDto = new QnaResponseDto();
            responseDto.setQnaIndex(qna.getQnaIndex());
            responseDto.setQuestionTitle(qna.getQuestionTitle());
            responseDto.setQuestionDesc(qna.getQuestionDesc());
            responseDto.setAnswerTitle(qna.getAnswerTitle());
            responseDto.setAnswerDesc(qna.getAnswerDesc());
            responseDto.setQnaCreateTime(qna.getQnaCreateTime());
            responseDto.setAnswerCreateTime(qna.getAnswerCreateTime());

            // 안전한 null 체크 - 더 강화된 버전
            try {
                if (qna.getQuestionUser() != null &&
                        qna.getQuestionUser().getUsersId() != null &&
                        qna.getQuestionUser().getUsersId().getName() != null) {
                    responseDto.setQuestionUserName(qna.getQuestionUser().getUsersId().getName());
                } else {
                    responseDto.setQuestionUserName("알 수 없음");
                }
            } catch (Exception e) {
                log.warn("질문자 이름 설정 중 오류: {}", e.getMessage());
                responseDto.setQuestionUserName("알 수 없음");
            }

            try {
                if (qna.getAnswerUser() != null &&
                        qna.getAnswerUser().getUsersId() != null &&
                        qna.getAnswerUser().getUsersId().getName() != null) {
                    responseDto.setAnswerUserName(qna.getAnswerUser().getUsersId().getName());
                } else {
                    responseDto.setAnswerUserName("관리자");
                }
            } catch (Exception e) {
                log.warn("답변자 이름 설정 중 오류: {}", e.getMessage());
                responseDto.setAnswerUserName("관리자");
            }

            responseDto.setIsAnswered(qna.getAnswerDesc() != null && !qna.getAnswerDesc().isEmpty());

            return responseDto;
        } catch (Exception e) {
            log.error("QnaResponseDto 변환 중 오류 발생", e);
            throw new RuntimeException("데이터 변환 중 오류가 발생했습니다.");
        }
    }
}