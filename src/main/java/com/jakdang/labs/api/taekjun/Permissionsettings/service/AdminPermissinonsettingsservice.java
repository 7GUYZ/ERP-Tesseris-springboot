package com.jakdang.labs.api.taekjun.Permissionsettings.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.taekjun.Permissionsettings.repository.AdminPermissionsettingsrepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.AdminTypeRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.ProgramRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.MenuRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateByIndexDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.BulkAuthorityDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.BulkAuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO;
import com.jakdang.labs.api.taekjun.admintypeinsert.repository.AdminTypeInsertRepository;
import com.jakdang.labs.entity.AuthorityType;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.adminType;
import com.jakdang.labs.entity.Program;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserRepository;
import com.jakdang.labs.api.taekjun.adminmypage.repository.UpdateUserLogJtjRepo;
import com.jakdang.labs.entity.UpdateUserLog;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminPermissinonsettingsservice {

    private final AdminTypeInsertRepository adminTypeInsertRepository;
    private final AdminPermissionsettingsrepository repository;
    private final AdminTypeRepository adminTypeRepository;
    private final ProgramRepository programRepository;
    private final MenuRepository menuRepository;
    private final UserTesserisRepository userRepository;
    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UpdateUserLogJtjRepo updateUserLogRepository;
    private final AlarmSvc alarmSvc;

    public AdminPermissinonsettingsservice(AdminPermissionsettingsrepository repository,
            AdminTypeRepository adminTypeRepository,
            ProgramRepository programRepository,
            MenuRepository menuRepository,
            UserTesserisRepository userRepository,
            @Qualifier("permissionsettingsUserRepository") UserRepository usersRepository,
            PasswordEncoder passwordEncoder,
            UpdateUserLogJtjRepo updateUserLogRepository,
            AlarmSvc alarmSvc, AdminTypeInsertRepository adminTypeInsertRepository) {
        this.repository = repository;
        this.adminTypeRepository = adminTypeRepository;
        this.programRepository = programRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.updateUserLogRepository = updateUserLogRepository;
        this.alarmSvc = alarmSvc;
        this.adminTypeInsertRepository = adminTypeInsertRepository;
    }

    public List<AuthorityProgramDTO> getAuthorityPrograms(Integer adminTypeIndex) {
        return repository.findAuthorityProgramDTOByAdminTypeIndex(adminTypeIndex);
    }

    public List<MenuDTO> getMenu() {
        return menuRepository.findAllMenuDTOs();
    }

    @Transactional
    public boolean updateAuthority(AuthorityUpdateDTO updateDTO) {
        try {
            if (updateDTO.getAdminTypeIndex() == null) {
                log.error("adminTypeIndex가 null입니다.");
                return false;
            }
            if (updateDTO.getProgramIndex() == null) {
                log.error("programIndex가 null입니다.");
                return false;
            }

            // 실제 DB에서 adminType과 program 조회
            var adminTypeOpt = adminTypeRepository.findByAdminTypeIndex(updateDTO.getAdminTypeIndex());
            var programOpt = programRepository.findById(updateDTO.getProgramIndex());

            if (adminTypeOpt.isEmpty() || programOpt.isEmpty()) {
                log.error("AdminType 또는 Program이 존재하지 않습니다. adminTypeIndex: {}, programIndex: {}",
                        updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());
                return false;
            }

            adminType adminType = adminTypeOpt.get();
            Program program = programOpt.get();

            // 기존 권한이 존재하는지 확인
            var existingAuthority = repository.findByAdminTypeIndexAdminTypeIndexAndProgramIndexProgramIndex(
                    updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());

            if (existingAuthority.isPresent()) {
                // 기존 권한이 있으면 엔티티를 직접 수정
                AuthorityType authority = existingAuthority.get();

                // 변경 전 데이터 저장
                String beforeData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        adminType.getAdminTypeName(), program.getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

                authority.setInsertAuthority(updateDTO.getInsertAuthority());
                authority.setDeleteAuthority(updateDTO.getDeleteAuthority());
                authority.setUpdateAuthority(updateDTO.getUpdateAuthority());

                repository.save(authority);

                // 변경 후 데이터 저장
                String afterData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        adminType.getAdminTypeName(), program.getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

                // 로그 기록
                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
                updateUserLog.setInflictUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
                updateUserLog.setUpdateBeforeData(beforeData);
                updateUserLog.setUpdateAfterData(afterData);
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한수정");

                updateUserLogRepository.save(updateUserLog);

                // 권한 변경 알림 전송
                try {
                    alarmSvc.sendAuthorityChangedAlarm(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0, adminType.getAdminTypeName(), program.getProgramName(), "수정");
                    log.info("권한 수정 알림 전송 완료: {} 등급 - {} 메뉴", adminType.getAdminTypeName(), program.getProgramName());
                } catch (Exception e) {
                    log.error("권한 수정 알림 전송 실패: {}", e.getMessage());
                    // 알림 전송 실패해도 DB 저장은 성공으로 처리
                }

                return true;
            } else {
                // 기존 권한이 없으면 새로 생성
                AuthorityType newAuthority = new AuthorityType();

                // 실제 DB에서 조회한 엔티티로 관계 설정
                newAuthority.setAdminTypeIndex(adminType);
                newAuthority.setProgramIndex(program);
                newAuthority.setInsertAuthority(updateDTO.getInsertAuthority());
                newAuthority.setDeleteAuthority(updateDTO.getDeleteAuthority());
                newAuthority.setUpdateAuthority(updateDTO.getUpdateAuthority());

                repository.save(newAuthority);

                // 새로 생성된 권한 로그 기록
                String afterData = String.format("(등급:%s ,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        adminType.getAdminTypeName(), program.getProgramName(),
                        newAuthority.getInsertAuthority(), newAuthority.getDeleteAuthority(),
                        newAuthority.getUpdateAuthority());

                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
                updateUserLog.setInflictUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
                updateUserLog.setUpdateBeforeData("(등급:신규생성)");
                updateUserLog.setUpdateAfterData(afterData);
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한추가");

                updateUserLogRepository.save(updateUserLog);

                return true;
            }
        } catch (Exception e) {
            log.error("권한 업데이트 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * 권한 추가 (중복 조합 방지)
     */
    @Transactional
    public boolean insertAuthority(AuthorityUpdateDTO updateDTO, String authHeader) {
        try {
            // 필수 필드 검증
            if (updateDTO.getAdminTypeIndex() == null) {
                log.error("adminTypeIndex가 null입니다.");
                return false;
            }
            if (updateDTO.getProgramIndex() == null) {
                log.error("programIndex가 null입니다.");
                return false;
            }

            // 이미 존재하는지 확인
            var existing = repository.findByAdminTypeIndexAdminTypeIndexAndProgramIndexProgramIndex(
                    updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());
            if (existing.isPresent()) {
                log.warn("이미 해당 조합의 권한이 존재합니다. adminTypeIndex: {}, programIndex: {}", updateDTO.getAdminTypeIndex(),
                        updateDTO.getProgramIndex());
                return false;
            }
            // 엔티티 조회
            var adminTypeOpt = adminTypeRepository.findByAdminTypeIndex(updateDTO.getAdminTypeIndex());
            var programOpt = programRepository.findById(updateDTO.getProgramIndex());
            if (adminTypeOpt.isEmpty() || programOpt.isEmpty()) {
                log.error("AdminType 또는 Program이 존재하지 않습니다. adminTypeIndex: {}, programIndex: {}",
                        updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());
                return false;
            }
            var adminType = adminTypeOpt.get();
            var program = programOpt.get();
            // 새 권한 생성
            AuthorityType newAuthority = new AuthorityType();
            newAuthority.setAdminTypeIndex(adminType);
            newAuthority.setProgramIndex(program);
            newAuthority.setInsertAuthority(updateDTO.getInsertAuthority());
            newAuthority.setDeleteAuthority(updateDTO.getDeleteAuthority());
            newAuthority.setUpdateAuthority(updateDTO.getUpdateAuthority());
            repository.save(newAuthority);

            // 새로 생성된 권한 로그 기록
            String afterData = String.format("(등급:%s 수수료율:0.0,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                    adminType.getAdminTypeName(), program.getProgramName(),
                    newAuthority.getInsertAuthority(), newAuthority.getDeleteAuthority(),
                    newAuthority.getUpdateAuthority());

            UpdateUserLog updateUserLog = new UpdateUserLog();
            updateUserLog.setUpdateUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
            updateUserLog.setInflictUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
            updateUserLog.setUpdateBeforeData("(등급:신규생성 수수료율:0.0)");
            updateUserLog.setUpdateAfterData(afterData);
            updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
            updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한추가");

            updateUserLogRepository.save(updateUserLog);

            // 권한 변경 알림 전송
            try {
                alarmSvc.sendAuthorityChangedAlarm(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0, adminType.getAdminTypeName(), program.getProgramName(), "추가");
                log.info("권한 추가 알림 전송 완료: {} 등급 - {} 메뉴", adminType.getAdminTypeName(), program.getProgramName());
            } catch (Exception e) {
                log.error("권한 추가 알림 전송 실패: {}", e.getMessage());
                // 알림 전송 실패해도 DB 저장은 성공으로 처리
            }

            return true;
        } catch (Exception e) {
            log.error("권한 추가 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * 권한 삭제 (authorityTypeIndex로 삭제)
     */
    @Transactional
    public boolean deleteAuthority(Integer authorityTypeIndex, String authHeader) {
        try {
            Optional<AuthorityType> existing = repository.findById(Long.valueOf(authorityTypeIndex));
            if (existing.isEmpty()) {
                log.warn("삭제할 권한이 존재하지 않습니다. authorityTypeIndex: {}", authorityTypeIndex);
                return false;
            }

            AuthorityType authority = existing.get();

            // 삭제 전 데이터 저장
            String beforeData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                    authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                    authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

            repository.delete(authority);

            // 삭제 로그 기록
            UpdateUserLog updateUserLog = new UpdateUserLog();
            updateUserLog.setUpdateUserIndex(0); // 삭제자는 시스템으로 설정
            updateUserLog.setInflictUserIndex(0); // 영향을 받는 사용자도 시스템으로 설정
            updateUserLog.setUpdateBeforeData(beforeData);
            updateUserLog.setUpdateAfterData("(등급:삭제됨)");
            updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
            updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한삭제");

            updateUserLogRepository.save(updateUserLog);

            // 권한 변경 알림 전송
            try {
                alarmSvc.sendAuthorityChangedAlarm(0, authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(), "삭제");
                log.info("권한 삭제 알림 전송 완료: {} 등급 - {} 메뉴", authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName());
            } catch (Exception e) {
                log.error("권한 삭제 알림 전송 실패: {}", e.getMessage());
                // 알림 전송 실패해도 DB 저장은 성공으로 처리
            }

            return true;
        } catch (Exception e) {
            log.error("권한 삭제 중 오류 발생: ", e);
            return false;
        }
    }

    public List<ProgramDTO> getProgram(Integer menuIndex) {
        return programRepository.findProgramDTOsByMenuIndex(menuIndex);
    }
    
    public List<ProgramDTO> getAllPrograms() {
        return programRepository.findAllProgramDTOs();
    }

    public List<adminType> getAdminType() {
        return adminTypeInsertRepository.findAllByOrderByAdminTypeOrderAsc();
    }

    /**
     * 사용자 패스워드 검증 (users 테이블 기준, 암호화 비교)
     * 
     * @param userIndex user 테이블의 PK
     * @param password  평문 비밀번호
     * @return 검증 성공 여부
     */
    public boolean validateUserPassword(Integer userIndex, String password) {
        try {
            if (userIndex == null || password == null) {
                log.warn("userIndex 또는 password가 null입니다.");
                return false;
            }
            
            // userIndex로 User 엔티티 조회
            var userOpt = userRepository.findById(userIndex);
            if (userOpt.isEmpty()) {
                log.error("User가 존재하지 않습니다. userIndex: {}", userIndex);
                return false;
            }
            var user = userOpt.get();

            // UserEntity에서 직접 비밀번호 가져오기
            UserEntity users = user.getUsersId();
            if (users == null) {
                log.error("User에 연결된 Users가 없습니다. userIndex: {}", userIndex);
                return false;
            }

            String encodedPassword = users.getPassword();
            if (encodedPassword == null) {
                log.error("Users에 비밀번호가 없습니다. userIndex: {}", userIndex);
                return false;
            }

            // 평문과 암호화된 비밀번호 비교
            boolean matches = passwordEncoder.matches(password, encodedPassword);
            if (!matches) {
                log.error("비밀번호가 일치하지 않습니다. userIndex: {}", userIndex);
                return false;
            }
            log.info("사용자 패스워드 검증 성공. userIndex: {}", userIndex);
            return true;
        } catch (Exception e) {
            log.error("패스워드 검증 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * authorityTypeIndex로 권한 업데이트 (더 효율적인 방식)
     * 
     * @param updateDTO authorityTypeIndex 기반 업데이트 DTO
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateAuthorityByIndex(AuthorityUpdateByIndexDTO updateDTO) {
        try {
            // 필수 필드 검증
            if (updateDTO.getAuthorityTypeIndex() == null) {
                log.error("authorityTypeIndex가 null입니다.");
                return false;
            }

            // authorityTypeIndex로 직접 권한 조회
            Optional<AuthorityType> authorityOpt = repository.findById(Long.valueOf(updateDTO.getAuthorityTypeIndex()));

            if (authorityOpt.isEmpty()) {
                log.error("해당 authorityTypeIndex의 권한이 존재하지 않습니다. authorityTypeIndex: {}",
                        updateDTO.getAuthorityTypeIndex());
                return false;
            }

            AuthorityType authority = authorityOpt.get();

            // 변경 전 데이터 저장
            String beforeData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                    authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                    authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

            // 권한 정보 업데이트
            authority.setInsertAuthority(updateDTO.getInsertAuthority());
            authority.setDeleteAuthority(updateDTO.getDeleteAuthority());
            authority.setUpdateAuthority(updateDTO.getUpdateAuthority());

            repository.save(authority);

            // 변경 후 데이터 저장
            String afterData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                    authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                    authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

            // 로그 기록
            UpdateUserLog updateUserLog = new UpdateUserLog();
            updateUserLog.setUpdateUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
            updateUserLog.setInflictUserIndex(updateDTO.getUserIndex() != null ? updateDTO.getUserIndex() : 0);
            updateUserLog.setUpdateBeforeData(beforeData);
            updateUserLog.setUpdateAfterData(afterData);
            updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
            updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한수정");

            updateUserLogRepository.save(updateUserLog);

            log.info("권한 업데이트 성공. authorityTypeIndex: {}", updateDTO.getAuthorityTypeIndex());
            return true;

        } catch (Exception e) {
            log.error("권한 업데이트 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * 권한 일괄 추가 (성능 최적화)
     */
    @Transactional
    public boolean bulkInsertAuthorities(BulkAuthorityDTO bulkDTO) {
        try {
            if (bulkDTO.getAuthorities() == null || bulkDTO.getAuthorities().isEmpty()) {
                log.error("권한 목록이 비어있습니다.");
                return false;
            }

            List<AuthorityType> authoritiesToSave = new ArrayList<>();
            
            for (AuthorityUpdateDTO authDTO : bulkDTO.getAuthorities()) {
                // 필수 필드 검증
                if (authDTO.getAdminTypeIndex() == null || authDTO.getProgramIndex() == null) {
                    log.error("adminTypeIndex 또는 programIndex가 null입니다.");
                    continue;
                }

                // 이미 존재하는지 확인
                var existing = repository.findByAdminTypeIndexAdminTypeIndexAndProgramIndexProgramIndex(
                        authDTO.getAdminTypeIndex(), authDTO.getProgramIndex());
                if (existing.isPresent()) {
                    log.warn("이미 해당 조합의 권한이 존재합니다. adminTypeIndex: {}, programIndex: {}", 
                            authDTO.getAdminTypeIndex(), authDTO.getProgramIndex());
                    continue;
                }

                // 엔티티 조회
                var adminTypeOpt = adminTypeRepository.findByAdminTypeIndex(authDTO.getAdminTypeIndex());
                var programOpt = programRepository.findById(authDTO.getProgramIndex());
                if (adminTypeOpt.isEmpty() || programOpt.isEmpty()) {
                    log.error("AdminType 또는 Program이 존재하지 않습니다. adminTypeIndex: {}, programIndex: {}", 
                            authDTO.getAdminTypeIndex(), authDTO.getProgramIndex());
                    continue;
                }

                var adminType = adminTypeOpt.get();
                var program = programOpt.get();

                // 새 권한 생성
                AuthorityType newAuthority = new AuthorityType();
                newAuthority.setAdminTypeIndex(adminType);
                newAuthority.setProgramIndex(program);
                newAuthority.setInsertAuthority(authDTO.getInsertAuthority() != null ? authDTO.getInsertAuthority() : 1);
                newAuthority.setDeleteAuthority(authDTO.getDeleteAuthority() != null ? authDTO.getDeleteAuthority() : 1);
                newAuthority.setUpdateAuthority(authDTO.getUpdateAuthority() != null ? authDTO.getUpdateAuthority() : 1);

                authoritiesToSave.add(newAuthority);
            }

            if (authoritiesToSave.isEmpty()) {
                log.warn("저장할 권한이 없습니다.");
                return false;
            }

            // 일괄 저장
            repository.saveAll(authoritiesToSave);

            // 로그 기록 (일괄 처리)
            for (AuthorityType authority : authoritiesToSave) {
                String afterData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0);
                updateUserLog.setInflictUserIndex(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0);
                updateUserLog.setUpdateBeforeData("(등급:신규생성)");
                updateUserLog.setUpdateAfterData(afterData);
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한일괄추가");

                updateUserLogRepository.save(updateUserLog);

                // 권한 변경 알림 전송
                try {
                    alarmSvc.sendAuthorityChangedAlarm(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0, 
                            authority.getAdminTypeIndex().getAdminTypeName(), 
                            authority.getProgramIndex().getProgramName(), "일괄추가");
                } catch (Exception e) {
                    log.error("권한 일괄 추가 알림 전송 실패: {}", e.getMessage());
                }
            }

            log.info("권한 일괄 추가 완료. 추가된 권한 수: {}", authoritiesToSave.size());
            return true;

        } catch (Exception e) {
            log.error("권한 일괄 추가 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * 권한 일괄 수정 (authorityTypeIndex 리스트로 수정)
     */
    @Transactional
    public boolean bulkUpdateAuthorities(BulkAuthorityUpdateDTO bulkDTO) {
        try {
            if (bulkDTO.getAuthorities() == null || bulkDTO.getAuthorities().isEmpty()) {
                log.error("수정할 권한 목록이 비어있습니다.");
                return false;
            }

            List<AuthorityType> authoritiesToUpdate = new ArrayList<>();
            Map<Integer, String> beforeDataMap = new HashMap<>();
            
            for (AuthorityUpdateByIndexDTO authDTO : bulkDTO.getAuthorities()) {
                // 필수 필드 검증
                if (authDTO.getAuthorityTypeIndex() == null) {
                    log.error("authorityTypeIndex가 null입니다.");
                    continue;
                }

                // authorityTypeIndex로 직접 권한 조회
                Optional<AuthorityType> authorityOpt = repository.findById(Long.valueOf(authDTO.getAuthorityTypeIndex()));

                if (authorityOpt.isEmpty()) {
                    log.error("해당 authorityTypeIndex의 권한이 존재하지 않습니다. authorityTypeIndex: {}",
                            authDTO.getAuthorityTypeIndex());
                    continue;
                }

                AuthorityType authority = authorityOpt.get();

                // 변경 전 데이터 저장
                String beforeData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());
                
                beforeDataMap.put(authDTO.getAuthorityTypeIndex(), beforeData);

                // 권한 정보 업데이트
                authority.setInsertAuthority(authDTO.getInsertAuthority() != null ? authDTO.getInsertAuthority() : authority.getInsertAuthority());
                authority.setDeleteAuthority(authDTO.getDeleteAuthority() != null ? authDTO.getDeleteAuthority() : authority.getDeleteAuthority());
                authority.setUpdateAuthority(authDTO.getUpdateAuthority() != null ? authDTO.getUpdateAuthority() : authority.getUpdateAuthority());

                authoritiesToUpdate.add(authority);
            }

            if (authoritiesToUpdate.isEmpty()) {
                log.warn("수정할 권한이 없습니다.");
                return false;
            }

            // 일괄 저장
            repository.saveAll(authoritiesToUpdate);

            // 로그 기록 (일괄 처리)
            for (AuthorityType authority : authoritiesToUpdate) {
                String afterData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

                // 해당 권한의 변경 전 데이터 가져오기
                String beforeData = beforeDataMap.getOrDefault(authority.getAuthorityTypeIndex().intValue(), "");

                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0);
                updateUserLog.setInflictUserIndex(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0);
                updateUserLog.setUpdateBeforeData(beforeData);
                updateUserLog.setUpdateAfterData(afterData);
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한일괄수정");

                updateUserLogRepository.save(updateUserLog);

                // 권한 변경 알림 전송
                try {
                    alarmSvc.sendAuthorityChangedAlarm(bulkDTO.getUserIndex() != null ? bulkDTO.getUserIndex() : 0, 
                            authority.getAdminTypeIndex().getAdminTypeName(), 
                            authority.getProgramIndex().getProgramName(), "일괄수정");
                } catch (Exception e) {
                    log.error("권한 일괄 수정 알림 전송 실패: {}", e.getMessage());
                }
            }

            log.info("권한 일괄 수정 완료. 수정된 권한 수: {}", authoritiesToUpdate.size());
            return true;

        } catch (Exception e) {
            log.error("권한 일괄 수정 중 오류 발생: ", e);
            return false;
        }
    }

    /**
     * 권한 일괄 삭제 (authorityTypeIndex 리스트로 삭제)
     */
    @Transactional
    public boolean bulkDeleteAuthorities(List<Integer> authorityTypeIndexes) {
        try {
            if (authorityTypeIndexes == null || authorityTypeIndexes.isEmpty()) {
                log.error("삭제할 권한 인덱스 목록이 비어있습니다.");
                return false;
            }

            List<AuthorityType> authoritiesToDelete = new ArrayList<>();
            
            // 삭제할 권한들 조회
            for (Integer index : authorityTypeIndexes) {
                Optional<AuthorityType> existing = repository.findById(Long.valueOf(index));
                if (existing.isPresent()) {
                    authoritiesToDelete.add(existing.get());
                } else {
                    log.warn("삭제할 권한이 존재하지 않습니다. authorityTypeIndex: {}", index);
                }
            }

            if (authoritiesToDelete.isEmpty()) {
                log.warn("삭제할 권한이 없습니다.");
                return false;
            }

            // 삭제 전 로그 기록
            for (AuthorityType authority : authoritiesToDelete) {
                String beforeData = String.format("(등급:%s,프로그램:%s,삽입권한:%d,삭제권한:%d,수정권한:%d)",
                        authority.getAdminTypeIndex().getAdminTypeName(), authority.getProgramIndex().getProgramName(),
                        authority.getInsertAuthority(), authority.getDeleteAuthority(), authority.getUpdateAuthority());

                UpdateUserLog updateUserLog = new UpdateUserLog();
                updateUserLog.setUpdateUserIndex(0);
                updateUserLog.setInflictUserIndex(0);
                updateUserLog.setUpdateBeforeData(beforeData);
                updateUserLog.setUpdateAfterData("(등급:일괄삭제됨)");
                updateUserLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
                updateUserLog.setUpdateDataValue("프로그램명:권한설정 ,기능:권한일괄삭제");

                updateUserLogRepository.save(updateUserLog);

                // 권한 변경 알림 전송
                try {
                    alarmSvc.sendAuthorityChangedAlarm(0, authority.getAdminTypeIndex().getAdminTypeName(), 
                            authority.getProgramIndex().getProgramName(), "일괄삭제");
                } catch (Exception e) {
                    log.error("권한 일괄 삭제 알림 전송 실패: {}", e.getMessage());
                }
            }

            // 일괄 삭제
            repository.deleteAll(authoritiesToDelete);

            log.info("권한 일괄 삭제 완료. 삭제된 권한 수: {}", authoritiesToDelete.size());
            return true;

        } catch (Exception e) {
            log.error("권한 일괄 삭제 중 오류 발생: ", e);
            return false;
        }
    }
}
