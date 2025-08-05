package com.jakdang.labs.api.taekjun.user_list.Dto;

import lombok.Data;

@Data
public class UserListSearchDTO {
    private String id;           // 아이디
    private String email;        // 이메일
    private String name;         // 이름
    private String phone;        // 핸드폰 번호
    private String userRole;     // 등급
    private String startDate;    // 등록일 시작
    private String endDate;      // 등록일 끝
    private String recommenderEmail;  // 추천인 이메일
    private String recommenderName;   // 추천인 이름
    private String recommenderGrade;  // 추천인 등급
} 