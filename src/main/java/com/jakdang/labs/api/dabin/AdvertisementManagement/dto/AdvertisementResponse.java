package com.jakdang.labs.api.dabin.AdvertisementManagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementResponse {
    private Integer advertisementIndex;
    private String userId;
    private String advertisementPhoto;
    private String advertisementUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime advertisementCreateTime;
} 