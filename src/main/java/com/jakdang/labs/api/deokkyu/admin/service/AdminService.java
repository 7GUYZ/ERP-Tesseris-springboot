package com.jakdang.labs.api.deokkyu.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListRequestDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.repository.AdminhdkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService {

    private final AdminhdkRepository adminRepository;

    /**
     * 관리자 리스트 조회 (필터 조건 포함)
     */
    public List<AdminListResponseDto> getAdminList(AdminListRequestDto requestDto) {
        try {
            // 필터 조건이 모두 null이거나 비어있는 경우 전체 리스트 조회
            if (isEmptyFilter(requestDto)) {
                return adminRepository.findAllAdminList();
            }

            // 필터 조건이 있는 경우 조건부 조회
            return adminRepository.findAdminListWithFilters(
                    requestDto.getAdminUserEmail(),
                    requestDto.getAdminUserName(),
                    requestDto.getAdminUserPhone(),
                    requestDto.getAdminTypeName(),
                    requestDto.getAdminRankName(),
                    requestDto.getAdminRegistrationDateStart(),
                    requestDto.getAdminRegistrationDateEnd()
            );

        } catch (Exception e) {
            log.error("관리자 리스트 조회 중 오류 발생", e);
            throw new RuntimeException("관리자 리스트 조회에 실패했습니다.", e);
        }
    }

    /**
     * 전체 관리자 리스트 조회 (필터 없음)
     */
    public List<AdminListResponseDto> getAllAdminList() {
        try {
            return adminRepository.findAllAdminList();
        } catch (Exception e) {
            log.error("전체 관리자 리스트 조회 중 오류 발생", e);
            throw new RuntimeException("전체 관리자 리스트 조회에 실패했습니다.", e);
        }
    }

    /**
     * 필터 조건이 모두 비어있는지 확인
     */
    private boolean isEmptyFilter(AdminListRequestDto requestDto) {
        return (requestDto.getAdminUserEmail() == null || requestDto.getAdminUserEmail().trim().isEmpty()) &&
               (requestDto.getAdminUserName() == null || requestDto.getAdminUserName().trim().isEmpty()) &&
               (requestDto.getAdminUserPhone() == null || requestDto.getAdminUserPhone().trim().isEmpty()) &&
               (requestDto.getAdminTypeName() == null || requestDto.getAdminTypeName().trim().isEmpty()) &&
               (requestDto.getAdminRankName() == null || requestDto.getAdminRankName().trim().isEmpty()) &&
               requestDto.getAdminRegistrationDateStart() == null &&
               requestDto.getAdminRegistrationDateEnd() == null;
    }
} 