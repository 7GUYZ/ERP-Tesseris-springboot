package com.jakdang.labs.api.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageThumbnailDTO {

    private String type;
    private String thumbnailName;
    private double duration;

}
