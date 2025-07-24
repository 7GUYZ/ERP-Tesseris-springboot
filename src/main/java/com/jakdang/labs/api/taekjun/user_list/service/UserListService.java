package com.jakdang.labs.api.taekjun.user_list.service;

import com.jakdang.labs.api.taekjun.user_list.Dto.UserListResponseDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserListSearchDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserUpdateRequestDTO;
import com.jakdang.labs.api.taekjun.user_list.repository.UserListJtjRepo;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.UserBank;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.api.taekjun.signin.repository.SuggestionUserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.jakdang.labs.entity.UpdateUserLog;
import com.jakdang.labs.api.taekjun.adminmypage.repository.UpdateUserLogJtjRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class UserListService {
    private final UserListJtjRepo userListJtjRepo;
    private final PasswordEncoder passwordEncoder;
    private final SuggestionUserRepository suggestionUserRepository;
    private final UpdateUserLogJtjRepo updateUserLogJtjRepo;
    private final ObjectMapper objectMapper;

    public List<UserListResponseDTO> getUserList() {
        try {
            System.out.println("회원 목록 조회 시작");
            List<Object[]> rawList = userListJtjRepo.findUserListRaw();
            System.out.println("조회된 데이터 개수: " + rawList.size());
            return rawList.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("회원 목록 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("회원 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    public List<UserListResponseDTO> getUserListWithSearch(UserListSearchDTO searchDTO) {
        // 공백 제거
        String id = searchDTO.getId() != null ? searchDTO.getId().trim() : null;
        String name = searchDTO.getName() != null ? searchDTO.getName().trim() : null;
        String phone = searchDTO.getPhone() != null ? searchDTO.getPhone().trim() : null;
        String userRole = searchDTO.getUserRole() != null ? searchDTO.getUserRole().trim() : null;
        String startDate = searchDTO.getStartDate() != null ? searchDTO.getStartDate().trim() : null;
        String endDate = searchDTO.getEndDate() != null ? searchDTO.getEndDate().trim() : null;
        
        System.out.println("검색 조건 - 이름: " + name);
        List<Object[]> rawList = userListJtjRepo.findUserListWithSearch(
            id,
            name,
            phone,
            userRole,
            startDate,
            endDate
        );
        System.out.println("검색 결과 개수: " + rawList.size());
        return rawList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserListResponseDTO mapToDto(Object[] row) {
        UserListResponseDTO dto = new UserListResponseDTO();
        dto.setUserIndex((Integer) row[0]);
        dto.setName((String) row[1]);
        dto.setEmail((String) row[2]);
        dto.setPhone((String) row[3]);
        dto.setNickname((String) row[4]);
        dto.setBirthday(row[5] != null ? row[5].toString() : null);
        dto.setGender((String) row[6]);
        dto.setUserRole((String) row[7]);
        dto.setBankName((String) row[8]);
        dto.setBankNumber((String) row[9]);
        dto.setBankHolder((String) row[10]);
        dto.setAddress((String) row[11]);
        dto.setDetailAddress((String) row[12]);
        dto.setRecommenderName((String) row[13]);
        dto.setRecommenderEmail((String) row[14]);
        dto.setSuggestionJoinDate(row[15] != null ? row[15].toString() : null);
        dto.setCmBalance(row[16] != null ? Integer.parseInt(row[16].toString()) : 0);
        dto.setRegistrationDate(row[17] != null ? row[17].toString() : null);
        
        return dto;
    }
    
    @Transactional
    public boolean updateUser(Integer userIndex, UserUpdateRequestDTO updateDTO, Integer adminUserIndex) {
        try {
            System.out.println("회원 수정 시작 - userIndex: " + userIndex);
            System.out.println("수정 데이터: " + updateDTO);

            // UserTesseris 엔티티 조회
            UserTesseris userTesseris = userListJtjRepo.findById(userIndex)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIndex));
            UserEntity userEntity = userTesseris.getUsersId();

            // 업데이트 전 값 저장
            Map<String, Object> beforeMap = new LinkedHashMap<>();
            beforeMap.put("이름", userEntity.getName());
            beforeMap.put("전화번호", userEntity.getPhone());
            beforeMap.put("생년월일", userTesseris.getUserBirthday());
            beforeMap.put("주소", userTesseris.getUserAddress());
            beforeMap.put("상세주소", userTesseris.getUserDetailAddress());
            beforeMap.put("계좌번호", userTesseris.getUserBankNumber());
            beforeMap.put("예금주", userTesseris.getUserBankHolder());
            beforeMap.put("등급", userTesseris.getUserRoleIndex()); // 등급(권한) 추가
            beforeMap.put("성별", userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderName() : null);
            // 필요시 추가 필드

            // 실제 업데이트
            boolean changed = false;
            boolean passwordChanged = false;
            if (updateDTO.getName() != null && !updateDTO.getName().trim().isEmpty() && !updateDTO.getName().equals(userEntity.getName())) {
                userEntity.setName(updateDTO.getName().trim());
                changed = true;
            }
            if (updateDTO.getPhone() != null && !updateDTO.getPhone().trim().isEmpty() && !updateDTO.getPhone().equals(userEntity.getPhone())) {
                userEntity.setPhone(updateDTO.getPhone().trim());
                changed = true;
            }
            if (updateDTO.getBirthday() != null && !updateDTO.getBirthday().trim().isEmpty()) {
                try {
                    if (userTesseris.getUserBirthday() == null || !java.time.LocalDate.parse(updateDTO.getBirthday()).equals(userTesseris.getUserBirthday())) {
                        userTesseris.setUserBirthday(java.time.LocalDate.parse(updateDTO.getBirthday()));
                        changed = true;
                    }
                } catch (Exception e) {
                    System.err.println("생년월일 파싱 오류: " + e.getMessage());
                }
            }
            if (updateDTO.getAddress() != null && !updateDTO.getAddress().trim().isEmpty() && !updateDTO.getAddress().equals(userTesseris.getUserAddress())) {
                userTesseris.setUserAddress(updateDTO.getAddress().trim());
                changed = true;
            }
            if (updateDTO.getDetailAddress() != null && !updateDTO.getDetailAddress().trim().isEmpty() && !updateDTO.getDetailAddress().equals(userTesseris.getUserDetailAddress())) {
                userTesseris.setUserDetailAddress(updateDTO.getDetailAddress().trim());
                changed = true;
            }
            if (updateDTO.getBankNumber() != null && !updateDTO.getBankNumber().trim().isEmpty() && !updateDTO.getBankNumber().equals(userTesseris.getUserBankNumber())) {
                userTesseris.setUserBankNumber(updateDTO.getBankNumber().trim());
                changed = true;
            }
            if (updateDTO.getBankHolder() != null && !updateDTO.getBankHolder().trim().isEmpty() && !updateDTO.getBankHolder().equals(userTesseris.getUserBankHolder())) {
                userTesseris.setUserBankHolder(updateDTO.getBankHolder().trim());
                changed = true;
            }
            String beforePassword = userEntity.getPassword();
            String afterPassword = beforePassword;
            String beforePasswordPlain = "알수없음";
            String afterPasswordPlain = updateDTO.getPassword();
            if (updateDTO.getPassword() != null && !updateDTO.getPassword().trim().isEmpty()) {
                afterPassword = passwordEncoder.encode(updateDTO.getPassword().trim());
                userEntity.setPassword(afterPassword);
                changed = true;
                passwordChanged = true;
            }

            userListJtjRepo.save(userTesseris);
            System.out.println("회원 정보 수정 완료");

            // 업데이트 후 값 저장
            Map<String, Object> afterMap = new LinkedHashMap<>();
            afterMap.put("이름", userEntity.getName());
            afterMap.put("전화번호", userEntity.getPhone());
            afterMap.put("생년월일", userTesseris.getUserBirthday());
            afterMap.put("주소", userTesseris.getUserAddress());
            afterMap.put("상세주소", userTesseris.getUserDetailAddress());
            afterMap.put("계좌번호", userTesseris.getUserBankNumber());
            afterMap.put("예금주", userTesseris.getUserBankHolder());
            afterMap.put("등급", userTesseris.getUserRoleIndex()); // 등급(권한) 추가
            afterMap.put("성별", userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderName() : null);
            // 필요시 추가 필드

            // 변경된 필드만 추출
            StringJoiner beforeJoiner = new StringJoiner(", ");
            StringJoiner afterJoiner = new StringJoiner(", ");
            for (String key : beforeMap.keySet()) {
                Object beforeVal = beforeMap.get(key);
                Object afterVal = afterMap.get(key);
                if (beforeVal == null && afterVal == null) continue;
                if (beforeVal == null || afterVal == null || !beforeVal.equals(afterVal)) {
                    beforeJoiner.add(key + ":" + (beforeVal != null ? beforeVal : ""));
                    afterJoiner.add(key + ":" + (afterVal != null ? afterVal : ""));
                }
            }
            // 비밀번호 변경 로그(평문)
            if (passwordChanged) {
                beforeJoiner.add("비밀번호:" + beforePasswordPlain);
                afterJoiner.add("비밀번호:" + afterPasswordPlain);
            }
            // 변경된 필드가 없으면 after 전체 기록
            if (afterJoiner.length() == 0) {
                for (String key : afterMap.keySet()) {
                    Object afterVal = afterMap.get(key);
                    afterJoiner.add(key + ":" + (afterVal != null ? afterVal : ""));
                }
                if (passwordChanged) {
                    afterJoiner.add("비밀번호:" + afterPasswordPlain);
                }
            }

            // 로그 저장
            UpdateUserLog log = new UpdateUserLog();
            log.setUpdateUserIndex(userIndex);
            log.setInflictUserIndex(adminUserIndex);
            log.setUpdateBeforeData(beforeJoiner.toString());
            log.setUpdateAfterData(afterJoiner.toString());
            log.setUpdateUserLogUpdateTime(java.time.LocalDateTime.now());
            log.setUpdateDataValue("프로그램명:유저리스트, 기능:수정");
            updateUserLogJtjRepo.save(log);

            return true;
        } catch (Exception e) {
            System.err.println("회원 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

} 