package com.jakdang.labs.api.taekjun.adminmypage.service;

import com.jakdang.labs.api.taekjun.adminmypage.dto.AdminMyPageResponseDto;
import com.jakdang.labs.api.taekjun.adminmypage.repository.AdminMyPageJtjRepo;
import com.jakdang.labs.api.taekjun.adminmypage.repository.UserEntityJtjRepo;
import com.jakdang.labs.api.taekjun.adminmypage.repository.AdminJTjRepo;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.Admin;
import com.jakdang.labs.api.auth.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMyPageService {
    
    private final AdminMyPageJtjRepo adminMyPageRepository;
    private final AdminJTjRepo adminRepository;
    private final UserEntityJtjRepo userEntityRepository;
    
    @Transactional(readOnly = true)
    public AdminMyPageResponseDto getAdminMyPageInfo(String userIndex) {
        log.info("어드민 마이페이지 정보 조회 - userIndex: {}", userIndex);
        
        try {
            Integer userIndexInt = Integer.parseInt(userIndex);
            
            // UserTesseris 정보 조회 (UserEntity 포함) - 어드민 권한(userRoleIndex = 4) 확인
            UserTesseris userTesseris = adminMyPageRepository.findUserTesserisWithDetails(userIndexInt, 4);
            if (userTesseris == null) {
                log.warn("사용자 정보를 찾을 수 없습니다 - userIndex: {}", userIndex);
                return null;
            }
            
            UserEntity userEntity = userTesseris.getUsersId();
            
            // Admin 정보 조회
            Admin admin = adminRepository.findByUserIndex(userIndexInt);
            
            // DTO 생성
            AdminMyPageResponseDto responseDto = AdminMyPageResponseDto.builder()
                    .userId(userEntity.getId())
                    .userName(userEntity.getName())
                    .userBirthday(userTesseris.getUserBirthday() != null ? userTesseris.getUserBirthday().toString() : null)
                    .userGender(userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderName() : null)
                    .userPhone(userEntity.getPhone())
                    .userEmail(userEntity.getEmail())
                    .userAddress(userTesseris.getUserAddress())
                    .userDetailAddress(userTesseris.getUserDetailAddress())
                    .build();
            
            // Admin 정보 설정
            if (admin != null) {
              
                responseDto.setAdminRegistrationDate(admin.getAdminRegistrationDate());
                
                // Admin 타입 이름 설정
                if (admin.getAdminTypeIndex() != null) {
                    responseDto.setAdminTypeName(admin.getAdminTypeIndex().getAdminTypeName());
                }
            }
            
            log.info("어드민 마이페이지 정보 조회 완료 - userIndex: {}", userIndex);
            return responseDto;
            
        } catch (NumberFormatException e) {
            log.error("userIndex 파라미터가 숫자가 아닙니다 - userIndex: {}", userIndex);
            return null;
        } catch (Exception e) {
            log.error("어드민 마이페이지 정보 조회 중 오류 발생 - userIndex: {}", userIndex, e);
            return null;
        }
    }
} 