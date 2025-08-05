package com.jakdang.labs.api.taekjun.Permissionsettings.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;


import com.jakdang.labs.api.taekjun.Permissionsettings.service.AdminPermissinonsettingsservice;
import com.jakdang.labs.entity.adminType;

import lombok.RequiredArgsConstructor;


import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.BulkAuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.BulkAuthorityDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/permissionsettings")
public class AdminPermissionsettings {

   
   private final AdminPermissinonsettingsservice AdminPermissinonsettingsservice;
    
    @GetMapping("/authorityprogramsbyadmin")
    public List<AuthorityProgramDTO> getAuthorityProgramsByAdmin(@RequestParam("adminTypeIndex") Integer adminTypeIndex) {
        return AdminPermissinonsettingsservice.getAuthorityPrograms(adminTypeIndex);
    }
    
    @PutMapping("/updateauthority")
    public ResponseEntity<String> updateAuthority(@RequestBody AuthorityUpdateDTO updateDTO) {
        // 필수 필드 검증
        if (updateDTO.getAdminTypeIndex() == null) {
            return ResponseEntity.badRequest().body("adminTypeIndex는 필수 필드입니다.");
        }
        if (updateDTO.getProgramIndex() == null) {
            return ResponseEntity.badRequest().body("programIndex는 필수 필드입니다.");
        }
        
        // 패스워드 검증이 필요한 경우 먼저 검증 수행
        if (updateDTO.getUserIndex() != null && updateDTO.getPassword() != null) {
            boolean passwordValid = AdminPermissinonsettingsservice.validateUserPassword(
                updateDTO.getUserIndex(), updateDTO.getPassword());
            if (!passwordValid) {
                return ResponseEntity.badRequest().body("사용자 인증에 실패했습니다. userIndex와 password를 확인해주세요.");
            }
        }
        
        boolean success = AdminPermissinonsettingsservice.updateAuthority(updateDTO);
        
        if (success) {
            return ResponseEntity.ok("권한이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("권한 업데이트에 실패했습니다.");
        }
    }

    @PostMapping("/insertauthority")
    public ResponseEntity<String> insertAuthority(@RequestBody AuthorityUpdateDTO insertDTO, @RequestHeader("Authorization") String authHeader) {
        // 필수 필드 검증
        if (insertDTO.getAdminTypeIndex() == null) {
            return ResponseEntity.badRequest().body("adminTypeIndex는 필수 필드입니다.");
        }
        if (insertDTO.getProgramIndex() == null) {
            return ResponseEntity.badRequest().body("programIndex는 필수 필드입니다.");
        }
        
        // 패스워드 검증이 필요한 경우 먼저 검증 수행
        if (insertDTO.getUserIndex() != null && insertDTO.getPassword() != null) {
            boolean passwordValid = AdminPermissinonsettingsservice.validateUserPassword(
                insertDTO.getUserIndex(), insertDTO.getPassword());
            if (!passwordValid) {
                return ResponseEntity.badRequest().body("사용자 인증에 실패했습니다. userIndex와 password를 확인해주세요.");
            }
        }
        
        boolean success = AdminPermissinonsettingsservice.insertAuthority(insertDTO, authHeader);
        if (success) {
            return ResponseEntity.ok("권한이 성공적으로 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("권한 추가에 실패했습니다. (이미 존재하거나 데이터 오류)");
        }
    }

    @PostMapping("/deleteauthority")
    public ResponseEntity<String> deleteAuthorityByPost(@RequestBody java.util.Map<String, Integer> body, @RequestHeader("Authorization") String authHeader) {
        Integer authorityTypeIndex = body.get("authorityTypeIndex");
        boolean success = AdminPermissinonsettingsservice.deleteAuthority(authorityTypeIndex, authHeader);
        if (success) {
            return ResponseEntity.ok("권한이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("권한 삭제에 실패했습니다. (존재하지 않음)");
        }
    }
    
    @GetMapping("/getmenu")
    public List<MenuDTO> getMenu() {
        return AdminPermissinonsettingsservice.getMenu();
    }

    @GetMapping("/getprogram")
    public List<ProgramDTO> getProgram(@RequestParam("menuIndex") Integer menuIndex ) {
        return AdminPermissinonsettingsservice.getProgram(menuIndex);
    }
    
    @GetMapping("/getallprograms")
    public List<ProgramDTO> getAllPrograms() {
        return AdminPermissinonsettingsservice.getAllPrograms();
    }

    @GetMapping("/getadmintype")
    public List<adminType> getadmintype() {
       return AdminPermissinonsettingsservice.getAdminType();
    }

    @PostMapping("/bulk-insert-authorities")
    public ResponseEntity<String> bulkInsertAuthorities(@RequestBody BulkAuthorityDTO bulkDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("권한 일괄 추가 요청 받음 - userIndex: {}, authorities 수: {}", 
                bulkDTO.getUserIndex(), bulkDTO.getAuthorities() != null ? bulkDTO.getAuthorities().size() : 0);
            
            // 필수 필드 검증
            if (bulkDTO.getAuthorities() == null || bulkDTO.getAuthorities().isEmpty()) {
                log.warn("추가할 권한 목록이 비어있습니다.");
                return ResponseEntity.badRequest().body("추가할 권한 목록이 비어있습니다.");
            }
            
            // 패스워드 검증이 필요한 경우 먼저 검증 수행
            if (bulkDTO.getUserIndex() != null && bulkDTO.getPassword() != null) {
                log.info("비밀번호 검증 시작 - userIndex: {}", bulkDTO.getUserIndex());
                boolean passwordValid = AdminPermissinonsettingsservice.validateUserPassword(
                    bulkDTO.getUserIndex(), bulkDTO.getPassword());
                if (!passwordValid) {
                    log.warn("사용자 인증 실패 - userIndex: {}", bulkDTO.getUserIndex());
                    return ResponseEntity.badRequest().body("사용자 인증에 실패했습니다. userIndex와 password를 확인해주세요.");
                }
                log.info("비밀번호 검증 성공 - userIndex: {}", bulkDTO.getUserIndex());
            }
            
            boolean success = AdminPermissinonsettingsservice.bulkInsertAuthorities(bulkDTO);
            if (success) {
                log.info("권한 일괄 추가 성공");
                return ResponseEntity.ok("권한이 성공적으로 일괄 추가되었습니다.");
            } else {
                log.warn("권한 일괄 추가 실패");
                return ResponseEntity.badRequest().body("권한 일괄 추가에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("권한 일괄 추가 중 예외 발생: ", e);
            return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/bulk-update-authorities")
    public ResponseEntity<String> bulkUpdateAuthorities(@RequestBody BulkAuthorityUpdateDTO bulkDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("권한 일괄 수정 요청 받음 - userIndex: {}, authorities 수: {}", 
                bulkDTO.getUserIndex(), bulkDTO.getAuthorities() != null ? bulkDTO.getAuthorities().size() : 0);
            
            // 필수 필드 검증
            if (bulkDTO.getAuthorities() == null || bulkDTO.getAuthorities().isEmpty()) {
                log.warn("수정할 권한 목록이 비어있습니다.");
                return ResponseEntity.badRequest().body("수정할 권한 목록이 비어있습니다.");
            }
            
            // 패스워드 검증이 필요한 경우 먼저 검증 수행
            if (bulkDTO.getUserIndex() != null && bulkDTO.getPassword() != null) {
                log.info("비밀번호 검증 시작 - userIndex: {}", bulkDTO.getUserIndex());
                boolean passwordValid = AdminPermissinonsettingsservice.validateUserPassword(
                    bulkDTO.getUserIndex(), bulkDTO.getPassword());
                if (!passwordValid) {
                    log.warn("사용자 인증 실패 - userIndex: {}", bulkDTO.getUserIndex());
                    return ResponseEntity.badRequest().body("사용자 인증에 실패했습니다. userIndex와 password를 확인해주세요.");
                }
                log.info("비밀번호 검증 성공 - userIndex: {}", bulkDTO.getUserIndex());
            }
            
            boolean success = AdminPermissinonsettingsservice.bulkUpdateAuthorities(bulkDTO);
            if (success) {
                log.info("권한 일괄 수정 성공");
                return ResponseEntity.ok("권한이 성공적으로 일괄 수정되었습니다.");
            } else {
                log.warn("권한 일괄 수정 실패");
                return ResponseEntity.badRequest().body("권한 일괄 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("권한 일괄 수정 중 예외 발생: ", e);
            return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다: " + e.getMessage());
        }
    }

} 