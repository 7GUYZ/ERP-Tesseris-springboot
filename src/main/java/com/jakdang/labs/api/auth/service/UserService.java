package com.jakdang.labs.api.auth.service;

import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.api.auth.dto.UserDTO;
import com.jakdang.labs.api.auth.dto.UserUpdateDTO;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.exceptions.handler.CustomException;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관리 서비스 클래스
 * 사용자 조회, 수정, 프로필 관리 등의 비즈니스 로직을 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    /**
     * 전체 사용자 목록 조회
     * 시스템에 등록된 모든 사용자 정보를 조회
     * 
     * @return 전체 사용자 목록
     */
    public ResponseDTO<List<UserDTO>> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return ResponseDTO.createSuccessResponse("Success", users.stream()
                .map(entity -> UserDTO.builder()
                        .id(entity.getId())
                        .activated(entity.getActivated())
                        .email(entity.getEmail())
                        .name(entity.getName())
                        .phone(entity.getPhone())
                        .nickname(entity.getNickname())
                        .role(entity.getRole().getRole())
                        .tCreatedAt(entity.getCreatedAt().atZone(java.time.ZoneId.systemDefault()))
                        .build()).toList()
        );
    }

    /**
     * 사용자 정보 수정
     * 특정 사용자의 정보를 업데이트
     * 
     * @param userId 수정할 사용자 ID
     * @param userUpdateDTO 수정할 사용자 정보 DTO
     * @return 수정된 사용자 정보
     */
    public ResponseDTO<UserDTO> updateUser(String userId, UserUpdateDTO userUpdateDTO) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.update(userUpdateDTO);
            user.setName(userUpdateDTO.getName());
            user.setActivated(userUpdateDTO.getActivated());
            user.setRole(RoleType.valueOf(userUpdateDTO.getRole()));
		    user.setImage(userUpdateDTO.getImage());
            userRepository.save(user);
            return ResponseDTO.createSuccessResponse("유저 정보 업데이트 성공", 
            UserDTO.builder()
            .id(user.getId())
            .activated(user.getActivated())
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .nickname(user.getNickname())
            .build());
        } else {
            throw new CustomException("유저를 찾을 수 없습니다.", -200);
        }
    }

    /**
     * 이메일로 사용자 조회
     * 이메일 주소를 통해 사용자 정보를 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 정보
     */
    public ResponseDTO<UserDTO> findUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow( () -> new CustomException("유저를 찾을 수 없습니다.", -200));

        return ResponseDTO.createSuccessResponse("유저 데이터 불러오기 성공", UserDTO.builder()
                .nickname(user.getNickname())
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build());
    }

    /**
     * 사용자 DTO 조회
     * 사용자 ID로 UserDTO 객체를 생성하여 반환
     * 
     * @param userId 사용자 ID
     * @return 사용자 DTO
     */
    public UserDTO getUserDTO(String userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .image(user.getImage())
                    .nickname(StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getName())
                    .build();
        }
        return userDTO;
    }

    /**
     * 사용자 엔티티 조회
     * 사용자 ID로 UserEntity 객체를 조회
     * 
     * @param requestUserId 사용자 ID
     * @return 사용자 엔티티
     */
    public UserEntity findById(String requestUserId) {
        return userRepository.findById(requestUserId).orElse(null);
    }

    /**
     * 사용자 프로필 조회
     * 사용자 ID로 프로필 정보를 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 프로필 정보
     */
    public ResponseDTO<UserDTO> getUserProfile(String id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("유저를 찾을 수 없습니다.", -200));

        return ResponseDTO.createSuccessResponse("유저 데이터 불러오기 성공", UserDTO.builder()
                .nickname(user.getNickname())
                .id(user.getId())
                .bio(user.getBio())
                .email(user.getEmail())
                .image(user.getImage())
                .build());
    }
}

