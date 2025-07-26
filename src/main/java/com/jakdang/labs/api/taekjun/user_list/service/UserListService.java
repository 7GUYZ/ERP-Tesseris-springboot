package com.jakdang.labs.api.taekjun.user_list.service;

import com.jakdang.labs.api.taekjun.user_list.Dto.UserListResponseDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserListSearchDTO;
import com.jakdang.labs.api.taekjun.user_list.Dto.UserUpdateRequestDTO;
import com.jakdang.labs.api.taekjun.user_list.repository.UserListJtjRepo;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.UserBank;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.api.taekjun.signin.repository.SuggestionUserRepository;
import com.jakdang.labs.api.jihun.common.config.ExcelDownloadConfig;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UserListService {
    private final UserListJtjRepo userListJtjRepo;
    private final SuggestionUserRepository suggestionUserRepository;
    private final ExcelDownloadConfig.ExcelDownloadProperties excelDownloadProperties;

    public List<UserListResponseDTO> getUserList() {
        List<Object[]> rawList = userListJtjRepo.findUserListRaw();
        
        return rawList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.toMap(
                    UserListResponseDTO::getUserIndex,
                    dto -> dto,
                    (existing, replacement) -> existing  // 기존 데이터 유지
                ))
                .values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getUserIndex(), a.getUserIndex()))  // 최신순 정렬
                .collect(Collectors.toList());
    }
    
    public List<UserListResponseDTO> getUserListWithSearch(UserListSearchDTO searchDTO) {
        List<Object[]> rawList = userListJtjRepo.findUserListWithSearch(
            searchDTO.getId(),
            searchDTO.getName(),
            searchDTO.getPhone(),
            searchDTO.getUserRole(),
            searchDTO.getStartDate(),
            searchDTO.getEndDate()
        );
        
        return rawList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.toMap(
                    UserListResponseDTO::getUserIndex,
                    dto -> dto,
                    (existing, replacement) -> existing  // 기존 데이터 유지
                ))
                .values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getUserIndex(), a.getUserIndex()))  // 최신순 정렬
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
        
        // 성별 매핑 수정 - user_gender_index를 문자열로 변환
        Integer genderIndex = (Integer) row[6];
        String gender = "";
        if (genderIndex != null) {
            if (genderIndex == 1) {
                gender = "남자";
            } else if (genderIndex == 2) {
                gender = "여자";
            }
        }
        dto.setGender(gender);
        
        dto.setUserRole((String) row[7]);
        dto.setBankName((String) row[8]);
        dto.setBankNumber((String) row[9]);
        dto.setBankHolder((String) row[10]);
        dto.setRecommenderName((String) row[11]);
        dto.setRecommenderEmail((String) row[12]);
        dto.setSuggestionJoinDate(row[13] != null ? row[13].toString() : null);
        dto.setCmBalance(row[14] != null ? Integer.parseInt(row[14].toString()) : 0);
        dto.setRegistrationDate(row[15] != null ? row[15].toString() : null);
        
        // 주소 매핑 수정 - user_zone_code(row[16])는 제외하고 실제 주소만 사용
        dto.setAddress((String) row[17]);  // u.user_address
        dto.setDetailAddress((String) row[18]);  // u.user_detail_address
        
        // null 값 처리 - 빈 문자열로 변환
        if (dto.getGender() == null || dto.getGender().trim().isEmpty()) {
            dto.setGender("");  // 빈 문자열로 설정
        }
        if (dto.getAddress() == null) dto.setAddress("");
        if (dto.getDetailAddress() == null) dto.setDetailAddress("");
        if (dto.getBankName() == null) dto.setBankName("");
        if (dto.getBankNumber() == null) dto.setBankNumber("");
        if (dto.getBankHolder() == null) dto.setBankHolder("");
        
        // 디버깅 로그 추가
        System.out.println("=== DTO 변환 정보 ===");
        System.out.println("UserIndex: " + dto.getUserIndex());
        System.out.println("GenderIndex: " + genderIndex);
        System.out.println("Gender: " + dto.getGender());
        System.out.println("Address: " + dto.getAddress());
        System.out.println("DetailAddress: " + dto.getDetailAddress());
        System.out.println("=========================");
        
        return dto;
    }
    
    public byte[] generateCsvFile(UserListSearchDTO searchDTO) throws IOException {
        List<UserListResponseDTO> userList;
        
        if (searchDTO != null && (searchDTO.getId() != null || searchDTO.getName() != null || 
                                 searchDTO.getPhone() != null || searchDTO.getUserRole() != null)) {
            // DATE 검색 조건 제외하고 검색
            UserListSearchDTO safeSearchDTO = new UserListSearchDTO();
            safeSearchDTO.setId(searchDTO.getId());
            safeSearchDTO.setName(searchDTO.getName());
            safeSearchDTO.setPhone(searchDTO.getPhone());
            safeSearchDTO.setUserRole(searchDTO.getUserRole());
            userList = getUserListWithSearch(safeSearchDTO);
        } else {
            userList = getUserList();
        }
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            
            // BOM 추가 (한글 깨짐 방지)
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            // 헤더 작성
            String[] headers = {
                "번호", "이름", "이메일", "전화번호", "닉네임", "생년월일", 
                "성별", "역할", "은행명", "계좌번호", "예금주", "추천인명", 
                "추천인이메일", "추천가입일", "CM잔액", "가입일"
            };
            
            writer.write(String.join(",", headers));
            writer.write("\n");
            
            // 데이터 작성 (ExcelDownloadConfig의 배치 크기 적용)
            int batchSize = excelDownloadProperties.getBatchSize();
            int processedCount = 0;
            
            for (int i = 0; i < userList.size(); i++) {
                UserListResponseDTO user = userList.get(i);
                String[] rowData = {
                    escapeCsvField(String.valueOf(i + 1)), // 행 번호 (1부터 시작)
                    escapeCsvField(user.getName()),
                    escapeCsvField(user.getEmail()),
                    escapeCsvField(user.getPhone()),
                    escapeCsvField(user.getNickname()),
                    escapeCsvField(user.getBirthday()),
                    escapeCsvField(user.getGender()),
                    escapeCsvField(user.getUserRole()),
                    escapeCsvField(user.getBankName()),
                    escapeCsvField(user.getBankNumber()),
                    escapeCsvField(user.getBankHolder()),
                    escapeCsvField(user.getRecommenderName()),
                    escapeCsvField(user.getRecommenderEmail()),
                    escapeCsvField(user.getSuggestionJoinDate()),
                    escapeCsvField(user.getCmBalance() != null ? user.getCmBalance().toString() : "0"),
                    escapeCsvField(user.getRegistrationDate())
                };
                
                writer.write(String.join(",", rowData));
                writer.write("\n");
                
                processedCount++;
                
                // 배치 크기에 도달하면 플러시
                if (processedCount % batchSize == 0) {
                    writer.flush();
                }
            }
            
            writer.flush();
            return outputStream.toByteArray();
        }
    }
    
    private String escapeCsvField(String value) {
        if (value == null) {
            return "";
        }
        
        // 쉼표나 따옴표가 포함된 경우 따옴표로 감싸기
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
    

    
    @Transactional
    public boolean updateUser(Integer userIndex, UserUpdateRequestDTO updateDTO) {
        try {
            // UserTesseris 엔티티 조회
            UserTesseris userTesseris = userListJtjRepo.findById(userIndex)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIndex));
            
            // UserEntity 업데이트
            UserEntity userEntity = userTesseris.getUsersId();
            if (updateDTO.getName() != null) {
                userEntity.setName(updateDTO.getName());
            }
            if (updateDTO.getPhone() != null) {
                userEntity.setPhone(updateDTO.getPhone());
            }
            
            // UserTesseris 업데이트
            if (updateDTO.getBirthday() != null && !updateDTO.getBirthday().trim().isEmpty()) {
                try {
                    userTesseris.setUserBirthday(java.time.LocalDate.parse(updateDTO.getBirthday()));
                } catch (Exception e) {
                    System.out.println("생년월일 파싱 오류: " + updateDTO.getBirthday());
                    // 생년월일 파싱 실패 시 null로 설정
                    userTesseris.setUserBirthday(null);
                }
            }
            
            // 성별 업데이트 추가
            if (updateDTO.getGender() != null && !updateDTO.getGender().trim().isEmpty()) {
                UserGender userGender = null;
                if ("남자".equals(updateDTO.getGender())) {
                    userGender = new UserGender();
                    userGender.setUserGenderIndex(1);
                } else if ("여자".equals(updateDTO.getGender())) {
                    userGender = new UserGender();
                    userGender.setUserGenderIndex(2);
                }
                userTesseris.setUserGender(userGender);
            }
            
            // 은행 정보 업데이트
            if (updateDTO.getBankNumber() != null) {
                userTesseris.setUserBankNumber(updateDTO.getBankNumber());
            }
            if (updateDTO.getBankHolder() != null) {
                userTesseris.setUserBankHolder(updateDTO.getBankHolder());
            }
            
            // 주소 업데이트
            if (updateDTO.getAddress() != null) {
                userTesseris.setUserAddress(updateDTO.getAddress());
            }
            if (updateDTO.getDetailAddress() != null) {
                userTesseris.setUserDetailAddress(updateDTO.getDetailAddress());
            }
            
            // 저장
            userListJtjRepo.save(userTesseris);
            
            return true;
        } catch (Exception e) {
            System.out.println("회원 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
    

} 