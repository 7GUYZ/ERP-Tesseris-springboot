package com.jakdang.labs.api.taekjun.user_list.Dto;

import lombok.Data;

@Data
public class UserListResponseDTO {
    private Integer userIndex;
    private String name;
    private String email;
    private String phone;
    private String nickname;
    private String birthday;
    private String gender;
    private String bankName;
    private String bankNumber;
    private String bankHolder;
    private String storeName;
    private String recommenderName;
    private Integer recommenderId;
    private String suggestionJoinDate;
    private Integer cmBalance;
    private String registrationDate;
} 