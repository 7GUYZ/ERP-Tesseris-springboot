package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminListDTO {
    private Integer userIndex;
    private String userId;
    private String adminTypeName;
    private Integer adminTypeOrder;
    private String name;
}
