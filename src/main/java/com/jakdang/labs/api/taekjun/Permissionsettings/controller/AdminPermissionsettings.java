package com.jakdang.labs.api.taekjun.Permissionsettings.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;


import com.jakdang.labs.api.taekjun.Permissionsettings.service.AdminPermissinonsettingsservice;
import com.jakdang.labs.entity.adminType;

import lombok.RequiredArgsConstructor;


import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityUpdateByIndexDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO;


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
    public ResponseEntity<String> insertAuthority(@RequestBody AuthorityUpdateDTO insertDTO) {
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
        
        boolean success = AdminPermissinonsettingsservice.insertAuthority(insertDTO);
        if (success) {
            return ResponseEntity.ok("권한이 성공적으로 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("권한 추가에 실패했습니다. (이미 존재하거나 데이터 오류)");
        }
    }

    @PostMapping("/deleteauthority")
    public ResponseEntity<String> deleteAuthorityByPost(@RequestBody java.util.Map<String, Integer> body) {
        Integer authorityTypeIndex = body.get("authorityTypeIndex");
        boolean success = AdminPermissinonsettingsservice.deleteAuthority(authorityTypeIndex);
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

    @GetMapping("/getadmintype")
    public List<adminType> getadmintype() {
       return AdminPermissinonsettingsservice.getAdminType();
    }

    /**
     * authorityTypeIndex로 권한 업데이트 (더 효율적인 방식)
     */
    @PutMapping("/update-authority-by-index")
    public ResponseEntity<String> updateAuthorityByIndex(@RequestBody AuthorityUpdateByIndexDTO updateDTO) {
        // 필수 필드 검증
        if (updateDTO.getAuthorityTypeIndex() == null) {
            return ResponseEntity.badRequest().body("authorityTypeIndex는 필수 필드입니다.");
        }
        
        // 패스워드 검증이 필요한 경우 먼저 검증 수행
        if (updateDTO.getUserIndex() != null && updateDTO.getPassword() != null) {
            boolean passwordValid = AdminPermissinonsettingsservice.validateUserPassword(
                updateDTO.getUserIndex(), updateDTO.getPassword());
            if (!passwordValid) {
                return ResponseEntity.badRequest().body("사용자 인증에 실패했습니다. userIndex와 password를 확인해주세요.");
            }
        }
        
        boolean success = AdminPermissinonsettingsservice.updateAuthorityByIndex(updateDTO);
        
        if (success) {
            return ResponseEntity.ok("권한이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("권한 업데이트에 실패했습니다.");
        }
    }
} 