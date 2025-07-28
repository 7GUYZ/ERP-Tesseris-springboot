package com.jakdang.labs.api.taekjun.customermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {
    private List<Integer> customerIndexes;
    private String status;
} 