package com.jakdang.labs.api.jungeun.service;

import com.jakdang.labs.api.jungeun.dto.LoginAdminDTO;
import com.jakdang.labs.api.jungeun.repository.AdminLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminLjeSvc {
    private final AdminLjeRepo adminLjeRepo;

    public LoginAdminDTO findByUserIndex(Integer userIndex) {
        return adminLjeRepo.findByUserIndex(userIndex)
            .map(admin -> LoginAdminDTO.builder()
                .adminTypeIndex(admin.getAdminTypeIndex().getAdminTypeIndex()) // ManyToOne일 경우
                .adminTypeName(admin.getAdminTypeIndex().getAdminTypeName())   // 여기!
                .build())
            .orElse(null);
    }

}


