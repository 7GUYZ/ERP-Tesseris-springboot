package com.jakdang.labs.api.taekjun.user_list.Dto;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String name;                    // 이름
    private String birthday;                // 생년월일
    private String gender;                  // 성별
    private String phone;                   // 휴대폰 번호
    private String address;                 // 기본주소
    private String detailAddress;           // 상세주소
    private String bankName;                // 은행명
    private String bankNumber;              // 계좌번호
    private String bankHolder;              // 예금주
    private String password;                // 새 비밀번호 (어드민용)
} 