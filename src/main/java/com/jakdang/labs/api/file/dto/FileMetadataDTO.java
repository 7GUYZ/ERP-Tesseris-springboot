package com.jakdang.labs.api.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FileMetadataDTO {
    private String tmpName;
    private String fileName;
    private String encoding;
    private String mimetype;
    private Integer width;
    private Integer height;
    private String type;
    private Long size;
}
