package com.jakdang.labs.api.taekjun.storelist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCategoryDTO {
    private Integer categoryIndex;
    private String categoryName;
} 