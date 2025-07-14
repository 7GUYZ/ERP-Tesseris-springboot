package com.jakdang.labs.api.auth.controller;

import com.jakdang.labs.aop.RunningTime;
import com.jakdang.labs.api.auth.dto.UserDTO;
import com.jakdang.labs.api.auth.dto.UserUpdateDTO;
import com.jakdang.labs.api.auth.service.UserService;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관리 API 컨트롤러
 * 사용자 조회, 수정, 프로필 관리 등의 기능을 제공
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * 이메일로 사용자 조회 API
     * 이메일 주소를 통해 사용자 정보를 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 정보
     */
    @GetMapping(value = "/find/{email}")
    @RunningTime
    public ResponseEntity<ResponseDTO<UserDTO>> findUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok().body(userService.findUserByEmail(email));
    }

    /**
     * 전체 사용자 목록 조회 API
     * 시스템에 등록된 모든 사용자 정보를 조회
     * 
     * @return 전체 사용자 목록
     */
    @GetMapping(value = "/all")
    @RunningTime
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    /**
     * 사용자 정보 수정 API
     * 특정 사용자의 정보를 업데이트
     * 
     * @param id 수정할 사용자 ID
     * @param userUpdateDTO 수정할 사용자 정보 DTO
     * @return 수정된 사용자 정보
     */
    @PutMapping(value = "/update/{id}")
    @RunningTime
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(@PathVariable("id") String id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok().body(userService.updateUser(id, userUpdateDTO));
    }

    /**
     * 사용자 프로필 조회 API
     * 특정 사용자의 프로필 정보를 조회
     * 
     * @param id 조회할 사용자 ID
     * @return 사용자 프로필 정보
     */
    @GetMapping(value = "/profile/{id}")
    @RunningTime
    public ResponseEntity<ResponseDTO<UserDTO>> getUserProfile(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(userService.getUserProfile(id));
    }

    // 정은 추가 0710 (테스트용)
    @GetMapping(value = "/testBackend")
    @RunningTime
    public ResponseEntity<ResponseDTO<String>> testBackend() {
        return ResponseEntity.ok().body(ResponseDTO.createSuccessResponse("테스트 백엔드 성공", null));
    }
 }
