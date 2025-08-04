package com.jakdang.labs.api.taekjun.admintypeinsert.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTypeUpdateDTO {
    private Integer adminTypeIndex;
    private String adminTypeName;
    private Integer newOrder;
} 