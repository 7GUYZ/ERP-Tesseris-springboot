package com.jakdang.labs.api.sichan.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaRequestDto {

    private String questionTitle;
    private String questionDesc;
}