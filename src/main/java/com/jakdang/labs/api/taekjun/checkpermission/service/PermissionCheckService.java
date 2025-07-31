package com.jakdang.labs.api.taekjun.checkpermission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.taekjun.checkpermission.dto.PermissionCheckDTO;
import com.jakdang.labs.api.taekjun.checkpermission.repository.PermissionCheckRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    private final PermissionCheckRepository permissionCheckRepository;
    
    public PermissionCheckDTO checkPermissions(Integer adminTypeIndex, Integer programIndex) {
        try {
            System.out.println("권한 체크 서비스 시작 - adminTypeIndex: " + adminTypeIndex + ", programIndex: " + programIndex);
            
            List<Object[]> permissionsList = permissionCheckRepository.checkAllPermissions(adminTypeIndex, programIndex);
            System.out.println("DB 조회 결과: " + (permissionsList != null ? permissionsList.toString() : "null"));
            
            PermissionCheckDTO result = new PermissionCheckDTO();
            result.setAdminTypeIndex(adminTypeIndex);
            result.setProgramIndex(programIndex);
            
            if (permissionsList != null && !permissionsList.isEmpty()) {
                Object[] permissions = permissionsList.get(0); // 첫 번째 행 가져오기
                System.out.println("DB 조회 결과 상세: permissions[0]=" + (permissions != null && permissions.length > 0 ? permissions[0] : "null") +
                                 ", permissions[1]=" + (permissions != null && permissions.length > 1 ? permissions[1] : "null") +
                                 ", permissions[2]=" + (permissions != null && permissions.length > 2 ? permissions[2] : "null"));

                if (permissions != null && permissions.length >= 3) {
                    int hasInsert = (Integer)permissions[0];
                    int hasDelete = (Integer)permissions[1];
                    int hasUpdate = (Integer)permissions[2];

                    result.setHasInsertAuthority(hasInsert);
                    result.setHasDeleteAuthority(hasDelete);
                    result.setHasUpdateAuthority(hasUpdate);
                } else {
                    result.setHasInsertAuthority(0);
                    result.setHasDeleteAuthority(0);
                    result.setHasUpdateAuthority(0);
                }
            } else {
                result.setHasInsertAuthority(0);
                result.setHasDeleteAuthority(0);
                result.setHasUpdateAuthority(0);
            }
            
            System.out.println("권한 체크 결과: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("권한 체크 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            PermissionCheckDTO errorResult = new PermissionCheckDTO();
            errorResult.setAdminTypeIndex(adminTypeIndex);
            errorResult.setProgramIndex(programIndex);
            errorResult.setHasInsertAuthority(0);
            errorResult.setHasDeleteAuthority(0);
            errorResult.setHasUpdateAuthority(0);
            return errorResult;
        }
    }
} 