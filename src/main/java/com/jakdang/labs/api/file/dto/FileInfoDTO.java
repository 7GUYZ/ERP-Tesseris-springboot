package com.jakdang.labs.api.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileInfoDTO {

    private byte[] file;
    private String fileName;
    private String url;
    private String key;
    ImageThumbnailDTO thumbnail;
}
