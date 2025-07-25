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
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanDeleteRequestDTO;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessArea;
import com.jakdang.labs.entity.UserCm;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;

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

    public List<BusinessmanListResponseDTO> searchBusinessmanList(BusinessmanListSearchDTO searchDTO) {
        List<BusinessMan> list = businessmanListRepository.searchBusinessManList(
                searchDTO.getEmail(),
                searchDTO.getUserName(),
                searchDTO.getUserPhone(),
                searchDTO.getBusinessGradeName(),
                searchDTO.getBossEmail(),
                searchDTO.getBusinessAreaName(),
                searchDTO.getBusinessAreaLevel(),
                searchDTO.getBusinessManDistributionFlag()
        );
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private BusinessmanListResponseDTO toDto(BusinessMan bm) {
        UserTesseris user = bm.getUserIndex();
        UserBank userBank = user.getUserBank();
        BusinessmanListResponseDTO dto = new BusinessmanListResponseDTO();
        dto.setUserIndex(user.getUserIndex());
        dto.setEmail(user.getUsersId() != null ? user.getUsersId().getEmail() : null);
        dto.setUserName(user.getUsersId() != null ? user.getUsersId().getName() : null);
        dto.setUserPhone(user.getUsersId() != null ? user.getUsersId().getPhone() : null);
        
        // bossEmail 조회
        String bossEmail = null;
        if (bm.getBossUserIndex() != null) {
            Optional<UserTesseris> bossUser = userTesserisRepository.findById(bm.getBossUserIndex());
            if (bossUser.isPresent() && bossUser.get().getUsersId() != null) {
                bossEmail = bossUser.get().getUsersId().getEmail();
            }
        }
        dto.setBossEmail(bossEmail);
        dto.setBusinessGradeName(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeName() : null);
        dto.setBusinessManDistributionFlag(bm.getBusinessManDistributionFlag() != null && bm.getBusinessManDistributionFlag() ? "정상" : "정지");
        dto.setUserBankName(userBank != null ? userBank.getUserBankName() : null);
        dto.setUserBankNumber(user.getUserBankNumber());
        dto.setUserBankHolder(user.getUserBankHolder());
        dto.setBusinessAreaName(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaName() : null);
        dto.setBusinessAreaLevel(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaLevel() : null);
        dto.setBusinessGradeIndex(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeIndex() : null);
        dto.setBusinessAreaIndex(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaIndex() : null);
        return dto;
    }

    @Transactional
    public ResponseEntity<?> createBusinessman(BusinessmanCreateRequestDTO dto) {
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
        userTesseris.setUserBirthday(dto.getUserBirthday() != null ? LocalDate.parse(dto.getUserBirthday()) : null);
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
        businessMan.setBusinessManRegistrationDate(dto.getBusinessManRegistrationDate() != null ? LocalDate.parse(dto.getBusinessManRegistrationDate()) : null);
        businessMan.setBusinessManCreateDate(LocalDateTime.now());
        businessmanListRepository.save(businessMan);

        return ResponseEntity.ok().body("사업자 회원 등록 성공");
    }

    @Transactional
    public ResponseEntity<?> updateBusinessman(BusinessmanUpdateRequestDTO dto) {
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
        Optional<BusinessMan> businessManOpt = businessmanListRepository.findById(userTesseris.getUserIndex());
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
        
        UserEntity savedUserEntity = userEntityRepository.save(userEntity);
        
        // UserEntity의 updated_at 값 업데이트 (한국 시간)
        Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
        businessmanListRepository.updateUserTimestamp(savedUserEntity.getId(), koreanTime);

        // 6. UserTesseris 정보 업데이트
        if (dto.getUserBirthday() != null) {
            userTesseris.setUserBirthday(LocalDate.parse(dto.getUserBirthday()));
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
        
        // BusinessMan의 updated_at 값 업데이트 (한국 시간)
        businessmanListRepository.updateTimestamp(businessMan.getBusinessManIndex(), koreanTime);

        return ResponseEntity.ok().body("사업자 정보 수정 성공");
    }

    @Transactional
    public ResponseEntity<?> deleteBusinessman(BusinessmanDeleteRequestDTO dto) {
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
        Optional<BusinessMan> businessManOpt = businessmanListRepository.findById(userTesseris.getUserIndex());
        if (businessManOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("사업자 정보를 찾을 수 없습니다.");
        }

        BusinessMan businessMan = businessManOpt.get();

        // 4. 계정 비활성화 (activated = false)
        userEntity.setActivated(false);
        UserEntity savedUserEntity = userEntityRepository.save(userEntity);
        
        // UserEntity의 updated_at 값 업데이트 (한국 시간)
        Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
        businessmanListRepository.updateUserTimestamp(savedUserEntity.getId(), koreanTime);

        // 5. BusinessMan 배포 상태도 비활성화로 변경
        businessMan.setBusinessManDistributionFlag(false);
        businessmanListRepository.save(businessMan);
        
        // BusinessMan의 updated_at 값 업데이트 (한국 시간)
        businessmanListRepository.updateTimestamp(businessMan.getBusinessManIndex(), koreanTime);

        return ResponseEntity.ok().body("사업자 계정이 비활성화되었습니다.");
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
} 