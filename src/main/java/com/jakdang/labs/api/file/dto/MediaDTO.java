package com.jakdang.labs.api.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaDTO {

    private String id;
    private String fileId;
    private String fileUrl;
    private String fileThumb;
    private Integer seq;
    private Integer mediaType;
    private Integer mediaWidth;
    private Integer mediaHeight;
    private List<?> tags;

    @JsonIgnore
    private List<String> deleteTags;

    // TODO 현재구조  MEDIAS <- FILE -> ALBUM ?
}
