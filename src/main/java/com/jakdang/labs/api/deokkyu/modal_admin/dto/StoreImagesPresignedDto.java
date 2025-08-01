package com.jakdang.labs.api.deokkyu.modal_admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreImagesPresignedDto {
    
    private String businessLicensePhotoUrl;
    private String signPhotoUrl;
    private String frontPhotoUrl;
    
    private String businessLicensePhotoPresignedUrl;
    private String signPhotoPresignedUrl;
    private String frontPhotoPresignedUrl;
    
}