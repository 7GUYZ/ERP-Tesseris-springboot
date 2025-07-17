package com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController;

public class UserRoleDto {
    private Integer userRoleIndex;
    private String userRoleKorNm;

    public UserRoleDto() {}

    public UserRoleDto(Integer userRoleIndex, String userRoleKorNm) {
        this.userRoleIndex = userRoleIndex;
        this.userRoleKorNm = userRoleKorNm;
    }

    // getter, setter
    public Integer getUserRoleIndex() { return userRoleIndex; }
    public void setUserRoleIndex(Integer userRoleIndex) { this.userRoleIndex = userRoleIndex; }
    
    public String getUserRoleKorNm() { return userRoleKorNm; }
    public void setUserRoleKorNm(String userRoleKorNm) { this.userRoleKorNm = userRoleKorNm; }
} 