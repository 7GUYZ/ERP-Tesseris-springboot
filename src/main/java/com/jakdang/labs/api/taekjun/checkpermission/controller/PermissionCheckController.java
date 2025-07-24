package com.jakdang.labs.api.taekjun.checkpermission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.taekjun.checkpermission.dto.PermissionCheckDTO;
import com.jakdang.labs.api.taekjun.checkpermission.dto.PermissionCheckRequestDTO;
import com.jakdang.labs.api.taekjun.checkpermission.service.PermissionCheckService;

@RestController
@RequestMapping("/checkpermission")
@RequiredArgsConstructor
public class PermissionCheckController {
    private final PermissionCheckService permissionCheckService;
    
    @PostMapping
    public ResponseEntity<PermissionCheckDTO> checkPermission(@RequestBody PermissionCheckRequestDTO requestDTO) {
        try {
            System.out.println("권한 체크 요청 받음 - adminTypeIndex: " + requestDTO.getAdminTypeIndex() + ", programIndex: " + requestDTO.getProgramIndex());
            PermissionCheckDTO result = permissionCheckService.checkPermissions(requestDTO.getAdminTypeIndex(), requestDTO.getProgramIndex());
            if (result != null) {
                System.out.println("권한 체크 결과: " + result);
                return ResponseEntity.ok(result);
            } else {
                System.out.println("권한 체크 결과가 null입니다.");
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            System.err.println("권한 체크 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
} 