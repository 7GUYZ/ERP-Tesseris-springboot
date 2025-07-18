package com.jakdang.labs.api.taekjun.adminmypage.service;

import com.jakdang.labs.api.taekjun.adminmypage.dto.AdminMyPageResponseDto;
import com.jakdang.labs.api.taekjun.adminmypage.dto.PasswordChangeRequestDto;
import com.jakdang.labs.api.taekjun.adminmypage.repository.AdminMyPageJtjRepo;
import com.jakdang.labs.api.taekjun.adminmypage.repository.UserEntityJtjRepo;
import com.jakdang.labs.api.taekjun.adminmypage.repository.AdminJTjRepo;
import com.jakdang.labs.api.taekjun.adminmypage.repository.UpdateUserLogRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserGenderRepository;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.Admin;
import com.jakdang.labs.entity.UpdateUserLog;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final UpdateUserLogRepository updateUserLogRepository;
    private final UserGenderRepository userGenderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    
    /**
     * 어드민 마이페이지 정보 조회
     */
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
                    .userName(userEntity.getName())
                    .userBirthday(userTesseris.getUserBirthday() != null ? userTesseris.getUserBirthday().toString() : null)
                    .userGender(userTesseris.getUserGender() != null ? String.valueOf(userTesseris.getUserGender().getUserGenderIndex()) : null)
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
    
    /**
     * 어드민 마이페이지 정보 수정
     */
    @Transactional
    public boolean updateAdminMyPageInfo(String userIndex, AdminMyPageResponseDto updateData) {
        log.info("어드민 마이페이지 정보 수정 - userIndex: {}", userIndex);
        
        try {
            Integer userIndexInt = Integer.parseInt(userIndex);
            
            // UserTesseris 정보 조회
            UserTesseris userTesseris = adminMyPageRepository.findUserTesserisWithDetails(userIndexInt, 4);
            if (userTesseris == null) {
                log.warn("수정할 사용자 정보를 찾을 수 없습니다 - userIndex: {}", userIndex);
                return false;
            }
            
            UserEntity userEntity = userTesseris.getUsersId();
            
            // 변경된 데이터 추적을 위한 StringBuilder
            StringBuilder beforeData = new StringBuilder();
            StringBuilder afterData = new StringBuilder();
            
            // 정보 업데이트 (null이 아닌 경우에만)
            if (updateData.getUserName() != null && !updateData.getUserName().equals(userEntity.getName())) {
                beforeData.append("userName:").append(userEntity.getName());
                afterData.append("userName:").append(updateData.getUserName());
                userEntity.setName(updateData.getUserName());
            }
            if (updateData.getUserPhone() != null && !updateData.getUserPhone().equals(userEntity.getPhone())) {
                if (beforeData.length() > 0) {
                    beforeData.append(",");
                    afterData.append(",");
                }
                beforeData.append("userPhone:").append(userEntity.getPhone());
                afterData.append("userPhone:").append(updateData.getUserPhone());
                userEntity.setPhone(updateData.getUserPhone());
            }
            if (updateData.getUserEmail() != null && !updateData.getUserEmail().equals(userEntity.getEmail())) {
                if (beforeData.length() > 0) {
                    beforeData.append(",");
                    afterData.append(",");
                }
                beforeData.append("userEmail:").append(userEntity.getEmail());
                afterData.append("userEmail:").append(updateData.getUserEmail());
                userEntity.setEmail(updateData.getUserEmail());
            }
            if (updateData.getUserAddress() != null && !updateData.getUserAddress().equals(userTesseris.getUserAddress())) {
                if (beforeData.length() > 0) {
                    beforeData.append(",");
                    afterData.append(",");
                }
                beforeData.append("userAddress:").append(userTesseris.getUserAddress());
                afterData.append("userAddress:").append(updateData.getUserAddress());
                userTesseris.setUserAddress(updateData.getUserAddress());
            }
            if (updateData.getUserDetailAddress() != null && !updateData.getUserDetailAddress().equals(userTesseris.getUserDetailAddress())) {
                if (beforeData.length() > 0) {
                    beforeData.append(",");
                    afterData.append(",");
                }
                beforeData.append("userDetailAddress:").append(userTesseris.getUserDetailAddress());
                afterData.append("userDetailAddress:").append(updateData.getUserDetailAddress());
                userTesseris.setUserDetailAddress(updateData.getUserDetailAddress());
            }
            
            // userGender 업데이트 로직 추가
            if (updateData.getUserGender() != null) {
                log.info("userGender 업데이트 요청 - 현재: {}, 요청: {}", 
                    userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderIndex() : "null", 
                    updateData.getUserGender());
                
                // 현재 userGender 인덱스와 비교
                String currentGenderIndex = userTesseris.getUserGender() != null ? 
                    String.valueOf(userTesseris.getUserGender().getUserGenderIndex()) : null;
                
                log.info("성별 비교 - 현재: {}, 요청: {}", currentGenderIndex, updateData.getUserGender());
                
                if (!updateData.getUserGender().equals(currentGenderIndex)) {
                    log.info("성별 변경 감지 - 현재: {}, 새로: {}", currentGenderIndex, updateData.getUserGender());
                    
                    if (beforeData.length() > 0) {
                        beforeData.append(",");
                        afterData.append(",");
                    }
                    beforeData.append("userGender:").append(currentGenderIndex);
                    afterData.append("userGender:").append(updateData.getUserGender());
                    
                    // UserGender 엔티티를 찾아서 설정
                    try {
                        Integer newGenderIndex = Integer.parseInt(updateData.getUserGender());
                        UserGender newUserGender = userGenderRepository.findById(newGenderIndex)
                            .orElseThrow(() -> new RuntimeException("성별 정보를 찾을 수 없습니다. ID: " + newGenderIndex));
                        userTesseris.setUserGender(newUserGender);
                        log.info("성별 업데이트 완료 - 새 성별: {}", newUserGender.getUserGenderName());
                    } catch (NumberFormatException e) {
                        log.warn("잘못된 성별 인덱스 형식입니다: {}", updateData.getUserGender());
                    } catch (Exception e) {
                        log.error("성별 업데이트 중 오류 발생: {}", e.getMessage());
                    }
                } else {
                    log.info("성별 변경 없음 - 현재와 요청이 동일함");
                }
            } else {
                log.info("userGender 업데이트 요청 없음");
            }
            
            // 저장
            userEntityRepository.save(userEntity);
            adminMyPageRepository.save(userTesseris);
            
            // UpdateUserLog 기록 (변경된 데이터가 있는 경우만)
            if (beforeData.length() > 0 || afterData.length() > 0) {
                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(userIndexInt);  // 업데이트하는 사람 (자기 자신)
                updateUserLog.setInflictUserIndex(userIndexInt); // 영향을 받는 사람 (자기 자신)
                updateUserLog.setUpdateBeforeData("("+beforeData.toString()+")");
                updateUserLog.setUpdateAfterData("("+afterData.toString()+")");
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("마이페이지 계정수정");
                
                updateUserLogRepository.save(updateUserLog);
            }
            
            log.info("어드민 마이페이지 정보 수정 완료 - userIndex: {}", userIndex);
            return true;
            
        } catch (NumberFormatException e) {
            log.error("userIndex 파라미터가 숫자가 아닙니다 - userIndex: {}", userIndex);
            return false;
        } catch (Exception e) {
            log.error("어드민 마이페이지 정보 수정 중 오류 발생 - userIndex: {}", userIndex, e);
            return false;
        }
    }
    
    /**
     * 패스워드 변경
     */
    @Transactional
    public boolean changePassword(String userIndex, PasswordChangeRequestDto passwordChangeRequest) {
        log.info("패스워드 변경 요청 - userIndex: {}", userIndex);
        
        try {
            Integer userIndexInt = Integer.parseInt(userIndex);
            
            // UserTesseris 정보 조회
            UserTesseris userTesseris = adminMyPageRepository.findUserTesserisWithDetails(userIndexInt, 4);
            if (userTesseris == null) {
                log.warn("사용자 정보를 찾을 수 없습니다 - userIndex: {}", userIndex);
                return false;
            }
            
            UserEntity userEntity = userTesseris.getUsersId();
            
            // 입력값 검증
            if (passwordChangeRequest.getCurrentPassword() == null || 
                passwordChangeRequest.getNewPassword() == null || 
                passwordChangeRequest.getConfirmPassword() == null) {
                log.warn("패스워드 변경 요청에 필수 필드가 누락되었습니다");
                return false;
            }
            
            // 현재 패스워드 검증
            if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), userEntity.getPassword())) {
                log.warn("현재 패스워드가 일치하지 않습니다 - userIndex: {}", userIndex);
                return false;
            }
            
            // 새 패스워드와 확인 패스워드 일치 검증
            if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
                log.warn("새 패스워드와 확인 패스워드가 일치하지 않습니다");
                return false;
            }
            
            // 새 패스워드가 현재 패스워드와 다른지 검증
            if (passwordEncoder.matches(passwordChangeRequest.getNewPassword(), userEntity.getPassword())) {
                log.warn("새 패스워드가 현재 패스워드와 동일합니다");
                return false;
            }
            

            
            // 새 패스워드 암호화 및 저장
            String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getNewPassword());
            userEntity.setPassword(encodedNewPassword);
            
            userEntityRepository.save(userEntity);
            
            // 패스워드 변경 로그 기록
            UpdateUserLog passwordChangeLog = new UpdateUserLog();
            passwordChangeLog.setUpdateUserIndex(userIndexInt);  // 업데이트하는 사람 (자기 자신)
            passwordChangeLog.setInflictUserIndex(userIndexInt); // 영향을 받는 사람 (자기 자신)
            passwordChangeLog.setUpdateBeforeData("비밀번호:" + passwordChangeRequest.getCurrentPassword());
            passwordChangeLog.setUpdateAfterData("비밀번호:" + passwordChangeRequest.getNewPassword());
            passwordChangeLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
            passwordChangeLog.setUpdateDataValue("프로그램명:마이페이지 ,기능:계정수정");
            
            updateUserLogRepository.save(passwordChangeLog);
            
            log.info("패스워드 변경 완료 - userIndex: {}", userIndex);
            return true;
            
        } catch (NumberFormatException e) {
            log.error("userIndex 파라미터가 숫자가 아닙니다 - userIndex: {}", userIndex);
            return false;
        } catch (Exception e) {
            log.error("패스워드 변경 중 오류 발생 - userIndex: {}", userIndex, e);
            return false;
        }
    }
} 