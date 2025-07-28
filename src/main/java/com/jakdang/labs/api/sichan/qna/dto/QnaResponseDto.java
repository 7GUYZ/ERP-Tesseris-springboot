package com.jakdang.labs.api.sichan.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaResponseDto {

    private Integer qnaIndex;
    private String questionTitle;
    private String questionDesc;
    private String answerTitle;
    private String answerDesc;
    private LocalDateTime qnaCreateTime;
    private LocalDateTime answerCreateTime;
    private String questionUserName;
    private String answerUserName;
    private Boolean isAnswered;
}