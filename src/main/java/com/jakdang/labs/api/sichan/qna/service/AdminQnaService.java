package com.jakdang.labs.api.sichan.qna.service;

import com.jakdang.labs.api.sichan.qna.dto.QnaRequestDto;
import com.jakdang.labs.api.sichan.qna.dto.QnaResponseDto;
import com.jakdang.labs.api.sichan.qna.repository.QnaRepository;
import com.jakdang.labs.entity.Qna;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminQnaService {

    private final QnaRepository qnaRepository;
    private final UserTesserisRepository userTesserisRepository;

    // QnA 목록 조회 (관리자용)
    @Transactional(readOnly = true)
    public List<QnaResponseDto> getQnaList(String searchType, String searchKeyword) {
        List<Qna> qnaList = qnaRepository.findAllWithUsers();

        // 검색 필터링
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            qnaList = qnaList.stream()
                    .filter(qna -> {
                        if ("title".equals(searchType)) {
                            return qna.getQuestionTitle() != null &&
                                    qna.getQuestionTitle().contains(searchKeyword);
                        } else if ("user".equals(searchType)) {
                            return qna.getQuestionUser() != null &&
                                    qna.getQuestionUser().getUsersId() != null &&
                                    qna.getQuestionUser().getUsersId().getName() != null &&
                                    qna.getQuestionUser().getUsersId().getName().contains(searchKeyword);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        return qnaList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // QnA 상세 조회 (관리자용)
    @Transactional(readOnly = true)
    public QnaResponseDto getQnaDetail(Integer qnaIndex) {
        Qna qna = qnaRepository.findById(qnaIndex)
                .orElseThrow(() -> new RuntimeException("QnA를 찾을 수 없습니다."));
        return convertToResponseDto(qna);
    }

    // QnA 답변 등록
    @Transactional
    public QnaResponseDto registerAnswer(Integer qnaIndex, QnaRequestDto requestDto, String userId) {
        Qna qna = qnaRepository.findById(qnaIndex)
                .orElseThrow(() -> new RuntimeException("QnA를 찾을 수 없습니다."));

        // userId로 UserTesseris 찾기
        UserTesseris adminUser = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다: " + userId));

        qna.setAnswerUser(adminUser);
        qna.setAnswerTitle(requestDto.getQuestionTitle());
        qna.setAnswerDesc(requestDto.getQuestionDesc());
        qna.setAnswerCreateTime(LocalDateTime.now());

        Qna savedQna = qnaRepository.save(qna);
        return convertToResponseDto(savedQna);
    }

    // 답변 대기 중인 QnA 목록
    @Transactional(readOnly = true)
    public List<QnaResponseDto> getWaitingQnaList() {
        List<Qna> waitingList = qnaRepository.findAllWithUsers().stream()
                .filter(qna -> qna.getAnswerDesc() == null || qna.getAnswerDesc().isEmpty())
                .collect(Collectors.toList());

        return waitingList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // 답변 완료된 QnA 목록
    @Transactional(readOnly = true)
    public List<QnaResponseDto> getCompletedQnaList() {
        List<Qna> completedList = qnaRepository.findAllWithUsers().stream()
                .filter(qna -> qna.getAnswerDesc() != null && !qna.getAnswerDesc().isEmpty())
                .collect(Collectors.toList());

        return completedList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Entity를 ResponseDto로 변환
    private QnaResponseDto convertToResponseDto(Qna qna) {
        QnaResponseDto responseDto = new QnaResponseDto();
        responseDto.setQnaIndex(qna.getQnaIndex());
        responseDto.setQuestionTitle(qna.getQuestionTitle());
        responseDto.setQuestionDesc(qna.getQuestionDesc());
        responseDto.setAnswerTitle(qna.getAnswerTitle());
        responseDto.setAnswerDesc(qna.getAnswerDesc());
        responseDto.setQnaCreateTime(qna.getQnaCreateTime());
        responseDto.setAnswerCreateTime(qna.getAnswerCreateTime());

        if (qna.getQuestionUser() != null && qna.getQuestionUser().getUsersId() != null) {
            responseDto.setQuestionUserName(qna.getQuestionUser().getUsersId().getName());
        }

        if (qna.getAnswerUser() != null && qna.getAnswerUser().getUsersId() != null) {
            responseDto.setAnswerUserName(qna.getAnswerUser().getUsersId().getName());
        }

        responseDto.setIsAnswered(qna.getAnswerDesc() != null && !qna.getAnswerDesc().isEmpty());

        return responseDto;
    }
}