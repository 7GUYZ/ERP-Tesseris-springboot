package com.jakdang.labs.api.dabin.BannerManagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponseDto {
    private Integer bannerIndex;
    private String userId;
    private String bannerPhoto;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bannerCreateTime;

    private Boolean bannerIsvisible;
} 