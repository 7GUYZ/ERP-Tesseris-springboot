package com.jakdang.labs.api.taekjun.admintypeinsert.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTypeInsertDTO {
    private String adminTypeName;
    private Integer insertPosition; // 삽입할 위치 (1-based)
} 