package com.jakdang.labs.api.jungeun.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.NaviAuthorityDTO;
import com.jakdang.labs.api.jungeun.repository.AuthorityLjeRepo;
import com.jakdang.labs.api.jungeun.repository.ProgramLjeRepo;
import com.jakdang.labs.entity.AuthorityType;
import com.jakdang.labs.entity.Program;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthSvc {
    private final AuthorityLjeRepo authorityRepo;
    private final ProgramLjeRepo programRepo;

    public ResponseDTO<List<NaviAuthorityDTO>> getAuthority(Integer adminTypeIndex){
        List<AuthorityType> authorityList = authorityRepo.findByAdminTypeIndex(adminTypeIndex);
    
        List<NaviAuthorityDTO> dtoList = authorityList.stream()
        .map(e -> {
            Integer programIndex = e.getProgramIndex().getProgramIndex();
            Program program = programRepo.findById(programIndex).orElse(null);
    
            // DTO의 빌더 사용!
            return NaviAuthorityDTO.builder()
                .adminTypeIndex(e.getAdminTypeIndex().getAdminTypeIndex())
                .programIndex(programIndex)
                .menuIndex(program != null ? program.getMenuIndex() : null)
                .build();
        })
        .collect(Collectors.toList());

        return ResponseDTO.createSuccessResponse("프로그램 및 메뉴 접근 권한 조회 성공", dtoList);
    }
}
