package com.jakdang.labs.api.deokkyu.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListRequestDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminCreateRequestDto;
import com.jakdang.labs.api.deokkyu.admin.repository.AdminhdkRepository;
import com.jakdang.labs.entity.Admin;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.adminType;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.AdminTypeRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserGenderJtjRepo;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.auth.dto.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService {

    private final AdminhdkRepository adminRepository;
    private final UserTesserisRepository userTesserisRepository;
    private final UserRepository userRepository;
    private final AdminTypeRepository adminTypeRepository;
    private final UserGenderJtjRepo userGenderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlarmSvc alarmSvc;
    private final JwtUtil jwtUtil;

    /**
     * 관리자 리스트 조회 (필터 조건 포함)
     */
    public List<AdminListResponseDto> getAdminList(AdminListRequestDto requestDto) {
        try {
            List<AdminListResponseDto> result;
            
            // 필터 조건이 모두 null이거나 비어있는 경우 전체 리스트 조회
            if (isEmptyFilter(requestDto)) {
                result = adminRepository.findAllAdminList();
            } else {
                // 필터 조건이 있는 경우 조건부 조회
                result = adminRepository.findAdminListWithFilters(
                        requestDto.getAdminUserEmail(),
                        requestDto.getAdminUserName(),
                        requestDto.getAdminUserPhone(),
                        requestDto.getAdminTypeName(),
                        requestDto.getAdminRegistrationDateStart(),
                        requestDto.getAdminRegistrationDateEnd()
                );
            }
            
            // 디버깅: 결과 로그 출력
            log.info("=== AdminList 조회 결과 ===");
            log.info("조회된 관리자 수: {}", result.size());
            for (AdminListResponseDto dto : result) {
                log.info("관리자: {} - 등록시간: {} (타입: {})", 
                    dto.getAdminUserEmail(), 
                    dto.getAdminRegistrationDate(), 
                    dto.getAdminRegistrationDate() != null ? dto.getAdminRegistrationDate().getClass().getSimpleName() : "null");
            }
            log.info("==========================");
            
            return result;

        } catch (Exception e) {
            log.error("관리자 리스트 조회 중 오류 발생", e);
            throw new RuntimeException("관리자 리스트 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 디버깅용: Admin 테이블만 조회
     */
    public void debugAdminTable() {
        try {
            List<Admin> admins = adminRepository.findAllAdmins();
            log.info("=== Admin 테이블 디버깅 ===");
            log.info("Admin 테이블 레코드 수: {}", admins.size());
            for (Admin admin : admins) {
                log.info("Admin: index={}, userIndex={}, registrationDate={}, adminTypeIndex={}", 
                    admin.getAdminIndex(),
                    admin.getUserIndex() != null ? admin.getUserIndex().getUserIndex() : "null",
                    admin.getAdminRegistrationDate(),
                    admin.getAdminTypeIndex() != null ? admin.getAdminTypeIndex().getAdminTypeIndex() : "null"
                );
            }
            log.info("==========================");
        } catch (Exception e) {
            log.error("Admin 테이블 디버깅 중 오류", e);
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
               requestDto.getAdminRegistrationDateStart() == null &&
               requestDto.getAdminRegistrationDateEnd() == null;
    }

    /**
     * 관리자 등록
     */
    @Transactional
    public boolean createAdmin(AdminCreateRequestDto createDto, String authHeader) {
        try {
            // 토큰에서 userId 추출하여 동작하는 관리자의 userIndex 구하기
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String userId = jwtUtil.getUserId(token);
            
            // userId로 UserEntity 조회
            Optional<UserEntity> currentUserEntityOpt = userRepository.findById(userId);
            if (currentUserEntityOpt.isEmpty()) {
                log.error("토큰에서 추출한 userId로 UserEntity를 찾을 수 없습니다: {}", userId);
                return false;
            }
            UserEntity currentUserEntity = currentUserEntityOpt.get();
            
            // UserEntity로 UserTesseris 조회
            Optional<UserTesseris> currentUserTesserisOpt = userTesserisRepository.findByUsersId(currentUserEntity);
            if (currentUserTesserisOpt.isEmpty()) {
                log.error("UserEntity에 해당하는 UserTesseris를 찾을 수 없습니다: {}", userId);
                return false;
            }
            
            // 실행하는 관리자의 userIndex 추출
            UserTesseris currentUserTesseris = currentUserTesserisOpt.get();
            Integer currentUserIndex = currentUserTesseris.getUserIndex();
            
            log.info("실행하는 관리자 userIndex: {}", currentUserIndex);

            log.info("관리자 등록 시작: {}", createDto.getAdminUserEmail());
            log.info("관리자 타입 인덱스: {}", createDto.getAdminTypeIndex());
            
            // 성별 데이터 초기화 (필요시)
            initializeGenderData();
            
            // 1. 필수 필드 검증
            if (createDto.getAdminUserEmail() == null || createDto.getAdminUserEmail().trim().isEmpty()) {
                log.error("이메일이 필수입니다.");
                return false;
            }
            if (createDto.getAdminPassword() == null || createDto.getAdminPassword().trim().isEmpty()) {
                log.error("비밀번호가 필수입니다.");
                return false;
            }
            if (createDto.getAdminTypeIndex() == null) {
                log.error("관리자 타입이 필수입니다.");
                return false;
            }
            
            // 2. 이메일 중복 체크
            if (userRepository.findByEmail(createDto.getAdminUserEmail()).isPresent()) {
                log.error("이미 존재하는 이메일: {}", createDto.getAdminUserEmail());
                return false;
            }
            
            // 3. AdminType 존재 여부 미리 확인
            adminType adminType = adminTypeRepository.findByAdminTypeIndex(createDto.getAdminTypeIndex())
                .orElse(null);
            if (adminType == null) {
                log.error("존재하지 않는 관리자 타입: {}", createDto.getAdminTypeIndex());
                return false;
            }
            
            // 4. UserEntity 생성 및 저장 (Users 테이블)
            UserEntity userEntity = UserEntity.builder()
                .email(createDto.getAdminUserEmail())
                .name(createDto.getAdminUserName())
                .phone(createDto.getAdminUserPhone())
                .password(passwordEncoder.encode(createDto.getAdminPassword()))
                .activated(true)
                .role(RoleType.ROLE_ADMIN)
                .advertise(false) // 광고 수신 동의 여부 (기본값: false)
                .build();
            
            UserEntity savedUserEntity = userRepository.save(userEntity);
            
            // created_at, updated_at 컬럼에 한국 시간 직접 설정 (네이티브 쿼리)
            java.time.Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
            userRepository.updateUserTimestampsNative(savedUserEntity.getId(), koreanTime, koreanTime);
            
            log.info("UserEntity 저장 완료: {}", savedUserEntity.getId());
            
            // 5. UserTesseris 생성 및 저장
            UserTesseris userTesseris = new UserTesseris();
            userTesseris.setUsersId(savedUserEntity);
            userTesseris.setUserRoleIndex(4); // 관리자 역할 (4: 관리자)
            userTesseris.setUserBirthday(createDto.getAdminUserBirthday() != null ? 
                java.time.LocalDate.parse(createDto.getAdminUserBirthday()) : null);
            
            // 성별 설정 - UserGender 엔티티 조회 및 설정
            if (createDto.getAdminUserGender() != null && !createDto.getAdminUserGender().trim().isEmpty()) {
                try {
                    Integer genderIndex = Integer.parseInt(createDto.getAdminUserGender());
                    log.info("성별 인덱스 파싱 성공: {}", genderIndex);
                    
                    // UserGender 엔티티 조회
                    UserGender userGender = userGenderRepository.findById(genderIndex)
                        .orElse(null);
                    
                    if (userGender != null) {
                        userTesseris.setUserGender(userGender);
                        log.info("성별 설정 완료: {} ({})", userGender.getUserGenderName(), genderIndex);
                    } else {
                        log.warn("존재하지 않는 성별 인덱스: {}", genderIndex);
                        // 기본값으로 설정하지 않고 null로 유지
                    }
                } catch (NumberFormatException e) {
                    log.warn("잘못된 성별 인덱스 형식: {}", createDto.getAdminUserGender());
                } catch (Exception e) {
                    log.error("성별 설정 중 오류 발생: {}", e.getMessage());
                }
            } else {
                log.info("성별 정보가 제공되지 않음");
            }
            
            // 주소 정보 설정
            if (createDto.getAdminAddress() != null && !createDto.getAdminAddress().trim().isEmpty()) {
                userTesseris.setUserAddress(createDto.getAdminAddress());
                log.info("주소 설정: {}", createDto.getAdminAddress());
            }
            
            if (createDto.getAdminDetailAddress() != null && !createDto.getAdminDetailAddress().trim().isEmpty()) {
                userTesseris.setUserDetailAddress(createDto.getAdminDetailAddress());
                log.info("상세주소 설정: {}", createDto.getAdminDetailAddress());
            }
            
            UserTesseris savedUserTesseris = userTesserisRepository.save(userTesseris);
            log.info("UserTesseris 저장 완료: {}", savedUserTesseris.getUserIndex());
            
            // 6. Admin 엔티티 생성 및 저장
            Admin admin = new Admin();
            admin.setUserIndex(savedUserTesseris);
            admin.setAdminRankName(null); // 사용하지 않음
            admin.setAdminTypeIndex(adminType);
            admin.setAdminRegistrationDate(java.time.LocalDateTime.now()); // 현재 시간으로 자동 설정
            
            log.info("Admin 등록일 자동 설정: {}", admin.getAdminRegistrationDate());
            
            // Admin 저장
            adminRepository.save(admin);
            log.info("Admin 저장 완료: {}", admin.getAdminIndex());
            
            log.info("관리자 등록 완료: {}", createDto.getAdminUserEmail());

            // 신규 관리자 등록 알림 서비스
            try {
                alarmSvc.sendAdminRegisterAlarm(currentUserIndex);
                log.info("신규 관리자 등록 알림 전송 완료");
            } catch (Exception e) {
                log.error("신규 관리자 등록 알림 전송 실패: {}", e.getMessage());
                // 알림 전송 실패해도 관리자 DB 저장은 성공으로 처리
            }
            return true;
            
        } catch (Exception e) {
            log.error("관리자 등록 실패: {}", e.getMessage(), e);
            // 트랜잭션 롤백을 위해 예외를 다시 던짐
            throw new RuntimeException("관리자 등록 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 성별 데이터 초기화 (기본 데이터가 없을 경우 추가)
     */
    @Transactional
    public void initializeGenderData() {
        try {
            // 기존 성별 데이터 확인
            List<UserGender> existingGenders = userGenderRepository.findAll();
            
            if (existingGenders.isEmpty()) {
                log.info("성별 데이터가 없습니다. 기본 데이터를 추가합니다.");
                
                // 남성 추가
                UserGender male = new UserGender();
                male.setUserGenderIndex(1);
                male.setUserGenderName("남자");
                userGenderRepository.save(male);
                
                // 여성 추가
                UserGender female = new UserGender();
                female.setUserGenderIndex(2);
                female.setUserGenderName("여자");
                userGenderRepository.save(female);
                
                log.info("성별 기본 데이터 추가 완료");
            } else {
                log.info("성별 데이터가 이미 존재합니다. 개수: {}", existingGenders.size());
            }
        } catch (Exception e) {
            log.error("성별 데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }
} 