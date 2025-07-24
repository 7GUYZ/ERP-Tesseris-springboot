package com.jakdang.labs.api.taekjun.user_list.Dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSearchDTO {
    private String zoneCode; // 우편번호
    private String address; // 기본주소
    private String roadAddress; // 도로명주소
    private String jibunAddress; // 지번주소
    private String latitude; // 위도
    private String longitude; // 경도
    private String addressName; // 주소명
    private String addressType; // 주소 타입
} 