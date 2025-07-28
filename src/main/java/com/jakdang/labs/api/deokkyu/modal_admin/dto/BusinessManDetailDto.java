package com.jakdang.labs.api.deokkyu.modal_admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessManDetailDto {
    

    
    // 1. 사용자 기본 정보 (users 테이블)
    private String userPassword;                        // 사용자 비밀번호
    private String userName;                            // 사용자 이름

    // 2, 3. 사용자 상세 정보 (user_tesseris 테이블)
    private String userBirthday;                        // 사용자 생년월일
    private Integer userGenderIndex;                    // 사용자 성별 인덱스
    

}
