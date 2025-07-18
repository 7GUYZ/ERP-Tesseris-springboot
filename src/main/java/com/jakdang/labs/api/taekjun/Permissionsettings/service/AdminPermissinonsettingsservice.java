package com.jakdang.labs.api.taekjun.Permissionsettings.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.taekjun.Permissionsettings.repository.AdminPermissionsettingsrepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.AdminTypeRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.ProgramRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.MenuRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateByIndexDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO;
import com.jakdang.labs.entity.AuthorityType;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.adminType;
import com.jakdang.labs.entity.Program;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminPermissinonsettingsservice {
    private final AdminPermissionsettingsrepository repository;
    private final AdminTypeRepository adminTypeRepository;
    private final ProgramRepository programRepository;
    private final MenuRepository menuRepository;
    private final UserTesserisRepository userRepository;
    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminPermissinonsettingsservice(AdminPermissionsettingsrepository repository,
                                         AdminTypeRepository adminTypeRepository,
                                         ProgramRepository programRepository,
                                         MenuRepository menuRepository,
                                         UserTesserisRepository userRepository,
                                         @Qualifier("permissionsettingsUserRepository") UserRepository usersRepository,
                                         PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.adminTypeRepository = adminTypeRepository;
        this.programRepository = programRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
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
                authority.setInsertAuthority(updateDTO.getInsertAuthority());
                authority.setDeleteAuthority(updateDTO.getDeleteAuthority());
                authority.setUpdateAuthority(updateDTO.getUpdateAuthority());
                
                repository.save(authority);
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
    public boolean insertAuthority(AuthorityUpdateDTO updateDTO) {
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
                log.warn("이미 해당 조합의 권한이 존재합니다. adminTypeIndex: {}, programIndex: {}", updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());
                return false;
            }
            // 엔티티 조회
            var adminTypeOpt = adminTypeRepository.findByAdminTypeIndex(updateDTO.getAdminTypeIndex());
            var programOpt = programRepository.findById(updateDTO.getProgramIndex());
            if (adminTypeOpt.isEmpty() || programOpt.isEmpty()) {
                log.error("AdminType 또는 Program이 존재하지 않습니다. adminTypeIndex: {}, programIndex: {}", updateDTO.getAdminTypeIndex(), updateDTO.getProgramIndex());
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
    public boolean deleteAuthority(Integer authorityTypeIndex) {
        try {
            Optional<AuthorityType> existing = repository.findById(Long.valueOf(authorityTypeIndex));
            if (existing.isEmpty()) {
                log.warn("삭제할 권한이 존재하지 않습니다. authorityTypeIndex: {}", authorityTypeIndex);
                return false;
            }
            repository.delete(existing.get());
            return true;
        } catch (Exception e) {
            log.error("권한 삭제 중 오류 발생: ", e);
            return false;
        }
    }

    public List<ProgramDTO> getProgram(Integer menuIndex) {
        return programRepository.findProgramDTOsByMenuIndex(menuIndex);
    }

    public List<adminType> getAdminType() {
       return adminTypeRepository.findAll();
    }
    
    /**
     * 사용자 패스워드 검증 (users 테이블 기준, 암호화 비교)
     * @param userIndex user 테이블의 PK
     * @param password 평문 비밀번호
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
                log.error("해당 authorityTypeIndex의 권한이 존재하지 않습니다. authorityTypeIndex: {}", updateDTO.getAuthorityTypeIndex());
                return false;
            }
            
            AuthorityType authority = authorityOpt.get();
            
            // 권한 정보 업데이트
            authority.setInsertAuthority(updateDTO.getInsertAuthority());
            authority.setDeleteAuthority(updateDTO.getDeleteAuthority());
            authority.setUpdateAuthority(updateDTO.getUpdateAuthority());
            
            repository.save(authority);
            log.info("권한 업데이트 성공. authorityTypeIndex: {}", updateDTO.getAuthorityTypeIndex());
            return true;
            
        } catch (Exception e) {
            log.error("권한 업데이트 중 오류 발생: ", e);
            return false;
        }
    }
}
