package com.jakdang.labs.api.deokkyu.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.deokkyu.admin.dto.ChatAdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.repository.AdminhdkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminChatService {

    private final AdminhdkRepository adminRepository;

    /**
     * 채팅용 관리자 리스트 조회
     * 
     * 데이터 흐름:
     * 1. Admin 테이블의 user_index 가져오기
     * 2. user_index로 UserTesseris 테이블에서 users_id 가져오기  
     * 3. users_id로 UserEntity(users 테이블)에서 name 가져오기
     * 4. admin_type_index로 AdminType 테이블에서 admin_type_name 가져오기
     * 5. admin 테이블의 admin_rank_name 가져오기
     * 
     * @return 채팅용 관리자 리스트
     */
    public List<ChatAdminListResponseDto> getChatAllAdminList() {
        try {
            log.info("채팅용 관리자 리스트 조회 시작");
            
            List<ChatAdminListResponseDto> adminList = adminRepository.findAllChatAdminList();
            
            log.info("채팅용 관리자 리스트 조회 완료 - 결과 개수: {}", adminList.size());
            
            // 로그로 첫 번째 결과 확인 (디버깅용)
            if (!adminList.isEmpty()) {
                ChatAdminListResponseDto firstAdmin = adminList.get(0);
                log.info("첫 번째 관리자 정보: adminName={}, adminUserIndex={}, adminTypeName={}, adminRankName={}", 
                        firstAdmin.getAdminName(), firstAdmin.getAdminUserIndex(), 
                        firstAdmin.getAdminTypeName(), firstAdmin.getAdminRankName());
            }
            
            return adminList;
            
        } catch (Exception e) {
            log.error("채팅용 관리자 리스트 조회 중 오류 발생", e);
            throw new RuntimeException("채팅용 관리자 리스트 조회에 실패했습니다.", e);
        }
    }
} 