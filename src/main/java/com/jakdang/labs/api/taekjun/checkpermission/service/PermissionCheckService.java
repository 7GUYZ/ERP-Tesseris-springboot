package com.jakdang.labs.api.taekjun.checkpermission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.taekjun.checkpermission.dto.PermissionCheckDTO;
import com.jakdang.labs.api.taekjun.checkpermission.repository.PermissionCheckRepository;

@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    private final PermissionCheckRepository permissionCheckRepository;
    
    public PermissionCheckDTO checkPermissions(Integer adminTypeIndex, Integer programIndex) {
        try {
            System.out.println("권한 체크 서비스 시작 - adminTypeIndex: " + adminTypeIndex + ", programIndex: " + programIndex);
            
            Object[] permissions = permissionCheckRepository.checkAllPermissions(adminTypeIndex, programIndex);
            System.out.println("DB 조회 결과: " + (permissions != null ? java.util.Arrays.toString(permissions) : "null"));
            
            PermissionCheckDTO result = new PermissionCheckDTO();
            result.setAdminTypeIndex(adminTypeIndex);
            result.setProgramIndex(programIndex);
            
            if (permissions != null && permissions.length >= 3) {
                boolean hasInsert = permissions[0] != null && (Integer) permissions[0] == 1;
                boolean hasDelete = permissions[1] != null && (Integer) permissions[1] == 1;
                boolean hasUpdate = permissions[2] != null && (Integer) permissions[2] == 1;
                
                result.setHasInsertAuthority(hasInsert);
                result.setHasDeleteAuthority(hasDelete);
                result.setHasUpdateAuthority(hasUpdate);
                
                System.out.println("권한 설정 완료 - insert: " + hasInsert + ", delete: " + hasDelete + ", update: " + hasUpdate);
            } else {
                result.setHasInsertAuthority(false);
                result.setHasDeleteAuthority(false);
                result.setHasUpdateAuthority(false);
                System.out.println("DB 조회 결과가 없어서 모든 권한을 false로 설정");
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("권한 체크 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 