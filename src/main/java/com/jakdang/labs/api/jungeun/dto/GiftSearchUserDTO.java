package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftSearchUserDTO {
    private Integer userIndex;
    private String userName; // 
    private String userEmail; // 우린 이메일로 검색이니까
    private Integer userRoleIndex; // 추가한 정보. 검색하면 보여주는 정보에 어떤 사용자인지 보여주면 좋을 듯 해서
    private String userPhone; // 핸드폰 번호 지역번호 + 끝자리(4개)만 공개할 예정.
}
