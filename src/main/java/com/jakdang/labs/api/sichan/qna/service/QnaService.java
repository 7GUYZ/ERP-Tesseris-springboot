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
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserTesserisRepository userTesserisRepository;

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
        return convertToResponseDto(savedQna);
    }

    // QnA 내역 조회
    @Transactional(readOnly = true)
    public List<QnaResponseDto> getInquiryList(String userIndex) {
        try {
            Integer userIndexInt = Integer.parseInt(userIndex);
            // 간단한 메서드 사용
            List<Qna> qnaList = qnaRepository.findByQuestionUser_UserIndexOrderByQnaCreateTimeDesc(userIndexInt);
            return qnaList.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        } catch (Exception e) {
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