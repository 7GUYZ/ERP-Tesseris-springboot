package com.jakdang.labs.api.taekjun.businessmanlist.service;

import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListResponseDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListSearchDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.BusinessmanListJtjRepo;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.UserBankJtjRepo;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.BusinessAreaJtjRepo;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserBank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanCreateRequestDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanUpdateRequestDTO;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessArea;
import com.jakdang.labs.entity.UserCm;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import com.jakdang.labs.api.taekjun.businessmanlist.controller.BusinessmanListController;
import com.jakdang.labs.api.jihun.common.config.ExcelDownloadConfig;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class BusinessmanListService {
    private final BusinessmanListJtjRepo businessmanListRepository;
    private final UserBankJtjRepo userBankJtjRepo;
    private final BusinessAreaJtjRepo businessAreaJtjRepo;
    private final UserTesserisRepository userTesserisRepository;
    private final AuthRepository userEntityRepository;
    @Autowired
    @Qualifier("userGenderJtjRepo")
    private JpaRepository<UserGender, Integer> userGenderRepository;
    @Autowired
    @Qualifier("businessGradekjyJtjRepo")
    private JpaRepository<BusinessGrade, Integer> businessGradeRepository;
    @Autowired
    @Qualifier("userCmJtjRepo")
    private JpaRepository<UserCm, Integer> userCmRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelDownloadConfig.ExcelDownloadProperties excelDownloadProperties;

    public List<BusinessmanListResponseDTO> searchBusinessmanList(BusinessmanListSearchDTO searchDTO) {
        System.out.println("=== BusinessmanListService.searchBusinessmanList 시작 ===");
        System.out.println("검색 조건: " + searchDTO);
        
        // 디버깅을 위한 추가 로그
        if (searchDTO != null && searchDTO.getUserName() != null) {
            System.out.println("이름 검색 조건: '" + searchDTO.getUserName() + "'");
        }
        
        try {
            // 디버깅을 위한 추가 로그
            System.out.println("전달되는 검색 조건들:");
            System.out.println("- email: " + (searchDTO != null ? searchDTO.getEmail() : "null"));
            System.out.println("- userName: " + (searchDTO != null ? searchDTO.getUserName() : "null"));
            System.out.println("- userPhone: " + (searchDTO != null ? searchDTO.getUserPhone() : "null"));
            System.out.println("- businessGradeName: " + (searchDTO != null ? searchDTO.getBusinessGradeName() : "null"));
            System.out.println("- bossEmail: " + (searchDTO != null ? searchDTO.getBossEmail() : "null"));
            System.out.println("- businessAreaName: " + (searchDTO != null ? searchDTO.getBusinessAreaName() : "null"));
            System.out.println("- businessAreaLevel: " + (searchDTO != null ? searchDTO.getBusinessAreaLevel() : "null"));
            System.out.println("- businessManDistributionFlag: " + (searchDTO != null ? searchDTO.getBusinessManDistributionFlag() : "null"));
            
            List<BusinessMan> list = businessmanListRepository.searchBusinessManList(
                    searchDTO != null ? searchDTO.getEmail() : null,
                    searchDTO != null ? searchDTO.getUserName() : null,
                    searchDTO != null ? searchDTO.getUserPhone() : null,
                    searchDTO != null ? searchDTO.getBusinessGradeName() : null,
                    searchDTO != null ? searchDTO.getBossEmail() : null,
                    searchDTO != null ? searchDTO.getBusinessAreaName() : null,
                    searchDTO != null ? searchDTO.getBusinessAreaLevel() : null,
                    searchDTO != null ? searchDTO.getBusinessManDistributionFlag() : null
            );
            
            System.out.println("조회된 사업자 수: " + list.size());
            
            if (list.size() > 0) {
                System.out.println("첫 번째 사업자 정보:");
                BusinessMan first = list.get(0);
                System.out.println("- UserIndex: " + first.getUserIndex().getUserIndex());
                System.out.println("- Email: " + first.getUserIndex().getUsersId().getEmail());
                System.out.println("- Activated: " + first.getUserIndex().getUsersId().getActivated());
                System.out.println("- UserRoleIndex: " + first.getUserIndex().getUserRoleIndex());
            }
            
            List<BusinessmanListResponseDTO> result = list.stream().map(this::toDto).collect(Collectors.toList());
            System.out.println("변환된 DTO 수: " + result.size());
            System.out.println("=== BusinessmanListService.searchBusinessmanList 완료 ===");
            
            return result;
        } catch (Exception e) {
            System.err.println("=== BusinessmanListService.searchBusinessmanList 에러 ===");
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<BusinessmanListResponseDTO> getBusinessmanList() {
        return searchBusinessmanList(null);
    }

    public List<BusinessmanListResponseDTO> getAllActiveBusinessmen() {
        System.out.println("=== BusinessmanListService.getAllActiveBusinessmen 시작 ===");
        
        try {
            List<BusinessMan> list = businessmanListRepository.findAllActiveBusinessmen();
            System.out.println("전체 활성 사업자 수: " + list.size());
            
            if (list.size() > 0) {
                System.out.println("첫 번째 사업자 정보:");
                BusinessMan first = list.get(0);
                System.out.println("- UserIndex: " + first.getUserIndex().getUserIndex());
                System.out.println("- Email: " + first.getUserIndex().getUsersId().getEmail());
                System.out.println("- Activated: " + first.getUserIndex().getUsersId().getActivated());
                System.out.println("- UserRoleIndex: " + first.getUserIndex().getUserRoleIndex());
            }
            
            List<BusinessmanListResponseDTO> result = list.stream().map(this::toDto).collect(Collectors.toList());
            System.out.println("변환된 DTO 수: " + result.size());
            System.out.println("=== BusinessmanListService.getAllActiveBusinessmen 완료 ===");
            
            return result;
        } catch (Exception e) {
            System.err.println("=== BusinessmanListService.getAllActiveBusinessmen 에러 ===");
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<BusinessmanListResponseDTO> getAllBusinessmen() {
        System.out.println("=== BusinessmanListService.getAllBusinessmen 시작 ===");
        
        try {
            List<BusinessMan> list = businessmanListRepository.findAllBusinessmen();
            System.out.println("전체 사업자 수: " + list.size());
            
            if (list.size() > 0) {
                System.out.println("첫 번째 사업자 정보:");
                BusinessMan first = list.get(0);
                System.out.println("- UserIndex: " + first.getUserIndex().getUserIndex());
                System.out.println("- Email: " + first.getUserIndex().getUsersId().getEmail());
                System.out.println("- Activated: " + first.getUserIndex().getUsersId().getActivated());
                System.out.println("- UserRoleIndex: " + first.getUserIndex().getUserRoleIndex());
            }
            
            List<BusinessmanListResponseDTO> result = list.stream().map(this::toDto).collect(Collectors.toList());
            System.out.println("변환된 DTO 수: " + result.size());
            System.out.println("=== BusinessmanListService.getAllBusinessmen 완료 ===");
            
            return result;
        } catch (Exception e) {
            System.err.println("=== BusinessmanListService.getAllBusinessmen 에러 ===");
            e.printStackTrace();
            throw e;
        }
    }

    private BusinessmanListResponseDTO toDto(BusinessMan bm) {
        UserTesseris user = bm.getUserIndex();
        UserBank userBank = user.getUserBank();
        BusinessmanListResponseDTO dto = new BusinessmanListResponseDTO();
        dto.setUserIndex(user.getUserIndex());
        dto.setEmail(user.getUsersId() != null ? user.getUsersId().getEmail() : null);
        dto.setUserName(user.getUsersId() != null ? user.getUsersId().getName() : null);
        dto.setUserPhone(user.getUsersId() != null ? user.getUsersId().getPhone() : null);
        
        // 생년월일, 성별 정보 추가
        String userBirthday = user.getUserBirthday() != null ? user.getUserBirthday().toString() : null;
        Integer userGenderIndex = user.getUserGender() != null ? user.getUserGender().getUserGenderIndex() : null;
        Integer userBankIndex = userBank != null ? userBank.getUserBankIndex() : null;
        
        dto.setUserBirthday(userBirthday);
        dto.setUserGenderIndex(userGenderIndex);
        
        // bossEmail과 bossName 조회
        String bossEmail = null;
        String bossName = null;
        if (bm.getBossUserIndex() != null) {
            Optional<UserTesseris> bossUser = userTesserisRepository.findById(bm.getBossUserIndex());
            if (bossUser.isPresent() && bossUser.get().getUsersId() != null) {
                bossEmail = bossUser.get().getUsersId().getEmail();
                bossName = bossUser.get().getUsersId().getName();
            }
        }
        dto.setBossEmail(bossEmail);
        dto.setBossName(bossName);
        dto.setBusinessGradeName(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeName() : null);
        dto.setBusinessManDistributionFlag(bm.getBusinessManDistributionFlag() != null && bm.getBusinessManDistributionFlag() ? "정상" : "정지");
        dto.setUserBankName(userBank != null ? userBank.getUserBankName() : null);
        dto.setUserBankNumber(user.getUserBankNumber());
        dto.setUserBankHolder(user.getUserBankHolder());
        dto.setUserBankIndex(userBank != null ? userBank.getUserBankIndex() : null);
        dto.setBusinessAreaName(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaName() : null);
        dto.setBusinessAreaLevel(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaLevel() : null);
        dto.setBusinessGradeIndex(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeIndex() : null);
        dto.setBusinessAreaIndex(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaIndex() : null);
        
        // 주소 정보 추가
        dto.setUserZoneCode(user.getUserZoneCode());
        dto.setUserAddress(user.getUserAddress());
        dto.setUserDetailAddress(user.getUserDetailAddress());
        
        return dto;
    }
    
    private BusinessmanListResponseDTO toDtoFromArray(Object[] row) {
        BusinessMan bm = (BusinessMan) row[0];
        String bossName = (String) row[1];
        String userZoneCode = (String) row[2];
        String userAddress = (String) row[3];
        String userDetailAddress = (String) row[4];
        
        UserTesseris user = bm.getUserIndex();
        UserBank userBank = user.getUserBank();
        BusinessmanListResponseDTO dto = new BusinessmanListResponseDTO();
        dto.setUserIndex(user.getUserIndex());
        dto.setEmail(user.getUsersId() != null ? user.getUsersId().getEmail() : null);
        dto.setUserName(user.getUsersId() != null ? user.getUsersId().getName() : null);
        dto.setUserPhone(user.getUsersId() != null ? user.getUsersId().getPhone() : null);
        
        // bossEmail과 bossName 설정
        String bossEmail = null;
        if (bm.getBossUserIndex() != null) {
            Optional<UserTesseris> bossUser = userTesserisRepository.findById(bm.getBossUserIndex());
            if (bossUser.isPresent() && bossUser.get().getUsersId() != null) {
                bossEmail = bossUser.get().getUsersId().getEmail();
            }
        }
        dto.setBossEmail(bossEmail);
        dto.setBossName(bossName); // 상사 이름 추가
        
        dto.setBusinessGradeName(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeName() : null);
        dto.setBusinessManDistributionFlag(bm.getBusinessManDistributionFlag() != null && bm.getBusinessManDistributionFlag() ? "정상" : "정지");
        dto.setUserBankName(userBank != null ? userBank.getUserBankName() : null);
        dto.setUserBankNumber(user.getUserBankNumber());
        dto.setUserBankHolder(user.getUserBankHolder());
        dto.setBusinessAreaName(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaName() : null);
        dto.setBusinessAreaLevel(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaLevel() : null);
        dto.setBusinessGradeIndex(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeIndex() : null);
        dto.setBusinessAreaIndex(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaIndex() : null);
        
        // 주소 정보 추가
        dto.setUserZoneCode(userZoneCode);
        dto.setUserAddress(userAddress);
        dto.setUserDetailAddress(userDetailAddress);
        
        return dto;
    }
    
    public byte[] generateCsvFile(BusinessmanListSearchDTO searchDTO) throws IOException {
        List<BusinessmanListResponseDTO> businessmanList;
        
        if (searchDTO != null && (searchDTO.getEmail() != null || searchDTO.getUserName() != null || 
                                 searchDTO.getUserPhone() != null || searchDTO.getBusinessGradeName() != null ||
                                 searchDTO.getBossEmail() != null || searchDTO.getBusinessAreaName() != null ||
                                 searchDTO.getBusinessAreaLevel() != null || searchDTO.getBusinessManDistributionFlag() != null)) {
            businessmanList = searchBusinessmanList(searchDTO);
        } else {
            businessmanList = searchBusinessmanList(new BusinessmanListSearchDTO());
        }
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            
            // BOM 추가 (한글 깨짐 방지)
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            // 헤더 작성
            String[] headers = {
                "번호", "이름", "이메일", "전화번호", "상사이름", "상사이메일", "사업등급", 
                "사업분야", "사업분야레벨", "분배상태", "은행명", "계좌번호", "예금주"
            };
            
            writer.write(String.join(",", headers));
            writer.write("\n");
            
            // 데이터 작성 (ExcelDownloadConfig의 배치 크기 적용)
            int batchSize = excelDownloadProperties.getBatchSize();
            int processedCount = 0;
            
            for (int i = 0; i < businessmanList.size(); i++) {
                BusinessmanListResponseDTO businessman = businessmanList.get(i);
                String[] rowData = {
                    escapeCsvField(String.valueOf(i + 1)), // 행 번호 (1부터 시작)
                    escapeCsvField(businessman.getUserName()),
                    escapeCsvField(businessman.getEmail()),
                    escapeCsvField(businessman.getUserPhone()),
                    escapeCsvField(businessman.getBossName()),
                    escapeCsvField(businessman.getBossEmail()),
                    escapeCsvField(businessman.getBusinessGradeName()),
                    escapeCsvField(businessman.getBusinessAreaName()),
                    escapeCsvField(businessman.getBusinessAreaLevel() != null ? businessman.getBusinessAreaLevel().toString() : ""),
                    escapeCsvField(businessman.getBusinessManDistributionFlag()),
                    escapeCsvField(businessman.getUserBankName()),
                    escapeCsvField(businessman.getUserBankNumber()),
                    escapeCsvField(businessman.getUserBankHolder())
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
    public ResponseEntity<?> createBusinessman(BusinessmanCreateRequestDTO dto) {
        System.out.println("=== BusinessmanListService.createBusinessman 시작 ===");
        System.out.println("입력 데이터: " + dto);
        
        try {
            // 1. 유효성 검사
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다.");
            }
            if (dto.getUserPw() == null || dto.getUserPw().isEmpty()) {
                return ResponseEntity.badRequest().body("비밀번호는 필수입니다.");
            }
            if (dto.getUserName() == null || dto.getUserName().isEmpty()) {
                return ResponseEntity.badRequest().body("이름은 필수입니다.");
            }

        // 2. 연관 엔티티 조회
        UserBank userBank = null;
        if (dto.getUserBankIndex() != null) {
            userBank = userBankJtjRepo.findById(dto.getUserBankIndex()).orElse(null);
        }
        
        UserGender userGender = null;
        if (dto.getUserGenderIndex() != null) {
            userGender = userGenderRepository.findById(dto.getUserGenderIndex()).orElse(null);
        }
        
        BusinessGrade businessGrade = null;
        if (dto.getBusinessGradeIndex() != null) {
            businessGrade = businessGradeRepository.findById(dto.getBusinessGradeIndex()).orElse(null);
        }
        
        BusinessArea businessArea = null;
        if (dto.getBusinessAreaIndex() != null) {
            businessArea = businessAreaJtjRepo.findById(dto.getBusinessAreaIndex()).orElse(null);
        }

        // 3. UserEntity 생성/수정 (email 기준)
        UserEntity userEntity = userEntityRepository.findByEmail(dto.getEmail()).orElse(UserEntity.builder().build());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(dto.getUserPw()));
        userEntity.setName(dto.getUserName());
        userEntity.setPhone(dto.getUserPhone());
        userEntity.setActivated(true);   // 무조건 1
        userEntity.setAdvertise(false); // 무조건 0
        // BaseEntity timestamp 자동 설정을 위해 저장
        UserEntity savedUserEntity = userEntityRepository.save(userEntity);
        
        // UserEntity의 created_at, updated_at 값 직접 설정
        Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
        businessmanListRepository.updateUserTimestamps(savedUserEntity.getId(), koreanTime, koreanTime);

        // 4. UserTesseris 생성/수정
        UserTesseris userTesseris = userTesserisRepository.findByUsersId(savedUserEntity).orElse(new UserTesseris());
        userTesseris.setUsersId(savedUserEntity);
        userTesseris.setUserBirthday(dto.getUserBirthday() != null && !dto.getUserBirthday().trim().isEmpty() ? LocalDate.parse(dto.getUserBirthday()) : null);
        userTesseris.setUserGender(userGender);
        userTesseris.setUserRoleIndex(2); // 사업자
        userTesseris.setUserBank(userBank);
        userTesseris.setUserBankNumber(dto.getUserBankNumber());
        userTesseris.setUserBankHolder(dto.getUserBankHolder());
        userTesseris.setUserZoneCode(dto.getUserZoneCode());
        userTesseris.setUserAddress(dto.getUserAddress());
        userTesseris.setUserDetailAddress(dto.getUserDetailAddress());
        
        // BaseEntity timestamp 자동 설정을 위해 저장
        UserTesseris savedUserTesseris = userTesserisRepository.save(userTesseris);

        // 5. UserCm 생성 (없으면)
        if (userCmRepository.findById(savedUserTesseris.getUserIndex()).isEmpty()) {
            UserCm userCm = UserCm.builder()
                    .userCmIndex(savedUserTesseris.getUserIndex())
                    .userCmDeposit(0)
                    .userCmWithdrawal(0)
                    .userCashDeposit(0)
                    .userCashWithdrawal(0)
                    .userCmpDeposit(0)
                    .userCmpWithdrawal(0)
                    .userCmpInit(0)
                    .userCmPincode("000000")
                    .build();
            userCmRepository.save(userCm);
        }

        // 6. 추천인(보스) email로 boss_user_index 찾기
        Integer bossUserIndex = null;
        if (dto.getBossEmail() != null && !dto.getBossEmail().isEmpty()) {
            UserEntity bossUserEntity = userEntityRepository.findByEmail(dto.getBossEmail()).orElse(null);
            if (bossUserEntity != null) {
                UserTesseris bossUserTesseris = userTesserisRepository.findByUsersId(bossUserEntity).orElse(null);
                if (bossUserTesseris != null) {
                    bossUserIndex = bossUserTesseris.getUserIndex();
                }
            }
        }

        // 7. BusinessMan 무조건 신규 등록
        // 이미 등록된 사업자인지 확인하는 로직은 일단 제거 (Repository에 해당 메서드가 없음)
        BusinessMan businessMan = new BusinessMan();
        businessMan.setUserIndex(savedUserTesseris);
        businessMan.setBossUserIndex(bossUserIndex);
        businessMan.setBusinessGrade(businessGrade);
        businessMan.setBusinessArea(businessArea);
        businessMan.setBusinessManDistributionFlag("정상".equals(dto.getBusinessManDistributionFlag()));
        businessMan.setBusinessManRegistrationDate(dto.getBusinessManRegistrationDate() != null && !dto.getBusinessManRegistrationDate().trim().isEmpty() ? LocalDate.parse(dto.getBusinessManRegistrationDate()) : null);
        businessMan.setBusinessManCreateDate(LocalDateTime.now());
        businessmanListRepository.save(businessMan);

        System.out.println("=== BusinessmanListService.createBusinessman 완료 ===");
        return ResponseEntity.ok().body("사업자 회원 등록 성공");
        } catch (Exception e) {
            System.err.println("=== BusinessmanListService.createBusinessman 에러 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("사업자 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> updateBusinessman(BusinessmanUpdateRequestDTO dto) {
        try {
            // 1. 유효성 검사
            if (dto.getUserIndex() == null) {
                return ResponseEntity.badRequest().body("사용자 인덱스는 필수입니다.");
            }

        // 2. 기존 사업자 정보 조회
        Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findById(dto.getUserIndex());
        if (userTesserisOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 사용자를 찾을 수 없습니다.");
        }

        UserTesseris userTesseris = userTesserisOpt.get();
        UserEntity userEntity = userTesseris.getUsersId();
        
        // 3. BusinessMan 정보 조회
        Optional<BusinessMan> businessManOpt = businessmanListRepository.findByUserIndex(userTesseris.getUserIndex());
        if (businessManOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("사업자 정보를 찾을 수 없습니다.");
        }

        BusinessMan businessMan = businessManOpt.get();

        // 4. 연관 엔티티 조회
        UserBank userBank = null;
        if (dto.getUserBankIndex() != null) {
            userBank = userBankJtjRepo.findById(dto.getUserBankIndex()).orElse(null);
        }
        
        UserGender userGender = null;
        if (dto.getUserGenderIndex() != null) {
            userGender = userGenderRepository.findById(dto.getUserGenderIndex()).orElse(null);
        }
        
        BusinessGrade businessGrade = null;
        if (dto.getBusinessGradeIndex() != null) {
            businessGrade = businessGradeRepository.findById(dto.getBusinessGradeIndex()).orElse(null);
        }
        
        BusinessArea businessArea = null;
        if (dto.getBusinessAreaIndex() != null) {
            businessArea = businessAreaJtjRepo.findById(dto.getBusinessAreaIndex()).orElse(null);
        }

        // 5. UserEntity 정보 업데이트
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            userEntity.setEmail(dto.getEmail());
        }
        if (dto.getUserName() != null && !dto.getUserName().isEmpty()) {
            userEntity.setName(dto.getUserName());
        }
        if (dto.getUserPhone() != null && !dto.getUserPhone().isEmpty()) {
            userEntity.setPhone(dto.getUserPhone());
        }
        
        // 비밀번호 업데이트 (선택사항)
        if (dto.getUserPw() != null && !dto.getUserPw().isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(dto.getUserPw()));
        }
        
        UserEntity savedUserEntity = userEntityRepository.save(userEntity);
        
        // UserEntity의 updated_at 값 업데이트 (한국 시간)
        Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
        businessmanListRepository.updateUserTimestamp(savedUserEntity.getId(), koreanTime);

        // 6. UserTesseris 정보 업데이트
        if (dto.getUserBirthday() != null && !dto.getUserBirthday().trim().isEmpty()) {
            try {
                userTesseris.setUserBirthday(LocalDate.parse(dto.getUserBirthday()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD 형식으로 입력해주세요)");
            }
        }
        if (userGender != null) {
            userTesseris.setUserGender(userGender);
        }
        if (userBank != null) {
            userTesseris.setUserBank(userBank);
        }
        if (dto.getUserBankNumber() != null) {
            userTesseris.setUserBankNumber(dto.getUserBankNumber());
        }
        if (dto.getUserBankHolder() != null) {
            userTesseris.setUserBankHolder(dto.getUserBankHolder());
        }
        if (dto.getUserZoneCode() != null) {
            userTesseris.setUserZoneCode(dto.getUserZoneCode());
        }
        if (dto.getUserAddress() != null) {
            userTesseris.setUserAddress(dto.getUserAddress());
        }
        if (dto.getUserDetailAddress() != null) {
            userTesseris.setUserDetailAddress(dto.getUserDetailAddress());
        }
        
        userTesserisRepository.save(userTesseris);

        // 7. 추천인(보스) email로 boss_user_index 찾기
        Integer bossUserIndex = null;
        if (dto.getBossEmail() != null && !dto.getBossEmail().isEmpty()) {
            UserEntity bossUserEntity = userEntityRepository.findByEmail(dto.getBossEmail()).orElse(null);
            if (bossUserEntity != null) {
                UserTesseris bossUserTesseris = userTesserisRepository.findByUsersId(bossUserEntity).orElse(null);
                if (bossUserTesseris != null) {
                    bossUserIndex = bossUserTesseris.getUserIndex();
                }
            }
        }

        // 8. 조직도 수정 (새로운 상사 설정)
        if (dto.getChangeOrganization() != null && dto.getChangeOrganization() && 
            dto.getNewBossEmail() != null && !dto.getNewBossEmail().isEmpty()) {
            
            // 새로운 상사 찾기
            UserEntity newBossUserEntity = userEntityRepository.findByEmail(dto.getNewBossEmail()).orElse(null);
            if (newBossUserEntity != null) {
                UserTesseris newBossUserTesseris = userTesserisRepository.findByUsersId(newBossUserEntity).orElse(null);
                if (newBossUserTesseris != null) {
                    Integer newBossUserIndex = newBossUserTesseris.getUserIndex();
                    
                    // 순환 참조 방지: 자기 자신을 상사로 설정할 수 없음
                    if (newBossUserIndex.equals(userTesseris.getUserIndex())) {
                        return ResponseEntity.badRequest().body("자기 자신을 상사로 설정할 수 없습니다.");
                    }
                    
                    // 순환 참조 방지: 하위 직원을 상사로 설정할 수 없음
                    if (isSubordinate(newBossUserIndex, userTesseris.getUserIndex())) {
                        return ResponseEntity.badRequest().body("하위 직원을 상사로 설정할 수 없습니다.");
                    }
                    
                    bossUserIndex = newBossUserIndex;
                }
            }
        }

        // 9. BusinessMan 정보 업데이트
        if (bossUserIndex != null) {
            businessMan.setBossUserIndex(bossUserIndex);
        }
        if (businessGrade != null) {
            businessMan.setBusinessGrade(businessGrade);
        }
        if (businessArea != null) {
            businessMan.setBusinessArea(businessArea);
        }
        if (dto.getBusinessManDistributionFlag() != null) {
            businessMan.setBusinessManDistributionFlag("정상".equals(dto.getBusinessManDistributionFlag()));
        }
        
        businessmanListRepository.save(businessMan);
        
        return ResponseEntity.ok().body("사업자 정보 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("사업자 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> deactivateBusinessman(Integer userIndex) {
        try {
            // UserTesseris 조회
            Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findById(userIndex);
            if (userTesserisOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }

            UserTesseris userTesseris = userTesserisOpt.get();
            UserEntity userEntity = userTesseris.getUsersId();
            
            // 활성화 상태만 false로 변경
            userEntity.setActivated(false);
            userEntityRepository.save(userEntity);
            
            // updated_at 업데이트
            Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
            businessmanListRepository.updateUserTimestamp(userEntity.getId(), koreanTime);
            
            return ResponseEntity.ok().body("사업자 계정이 비활성화되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("비활성화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isSubordinate(Integer bossUserIndex, Integer userIndex) {
        // BusinessMan을 통해 상하관계 확인
        Optional<BusinessMan> currentUserBusinessMan = businessmanListRepository.findById(userIndex);
        if (currentUserBusinessMan.isEmpty()) {
            return false;
        }
        
        BusinessMan businessMan = currentUserBusinessMan.get();
        Integer currentBossIndex = businessMan.getBossUserIndex();
        
        // 현재 사용자의 상사가 새로운 상사와 같으면 순환 참조
        if (currentBossIndex != null && currentBossIndex.equals(bossUserIndex)) {
            return true;
        }
        
        // 현재 사용자의 상사가 없으면 순환 참조 불가
        if (currentBossIndex == null) {
            return false;
        }
        
        // 현재 사용자의 상사의 상사를 재귀적으로 확인
        return isSubordinate(bossUserIndex, currentBossIndex);
    }

    // 사업자 등급 목록 조회
    public List<BusinessmanListController.BusinessGradeDTO> getBusinessGrades() {
        List<BusinessGrade> businessGrades = businessGradeRepository.findAll();
        return businessGrades.stream()
                .map(bg -> new BusinessmanListController.BusinessGradeDTO(
                        bg.getBusinessGradeIndex(),
                        bg.getBusinessGradeLevel(),
                        bg.getBusinessGradeName(),
                        bg.getBusinessGradeRate().doubleValue()
                ))
                .collect(Collectors.toList());
    }
    
    // 사업자 지역 목록 조회
    public List<BusinessmanListController.BusinessAreaDTO> getBusinessAreas() {
        List<BusinessArea> areas = businessAreaJtjRepo.findAll();
        return areas.stream()
                .map(area -> new BusinessmanListController.BusinessAreaDTO(
                        area.getBusinessAreaIndex(),
                        area.getBusinessAreaName(),
                        area.getBusinessAreaLevel()
                ))
                .collect(Collectors.toList());
    }

    public List<BusinessmanListController.BankDTO> getBanks() {
        List<UserBank> banks = userBankJtjRepo.findAll();
        return banks.stream()
                .map(bank -> new BusinessmanListController.BankDTO(
                        bank.getUserBankIndex(),
                        bank.getUserBankName()
                ))
                .collect(Collectors.toList());
    }
    

} 