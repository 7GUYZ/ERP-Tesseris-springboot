package com.jakdang.labs.api.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.jakdang.labs.api.auth.dto.*;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.security.jwt.service.TokenService;
import com.jakdang.labs.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 인증 서비스 클래스
 * 사용자 회원가입, 소셜 로그인, 사용자 정보 관리 등의 비즈니스 로직을 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder argon2PasswordEncoder;
    private final TokenService tokenService;
//    private final FirebaseAuth firebaseAuth;
    private final TokenUtils tokenUtils;

    /**
     * 사용자 회원가입 처리
     * 새로운 사용자를 시스템에 등록하고 비밀번호를 암호화하여 저장
     * 
     * @param signUpDTO 회원가입 정보 DTO
     * @return 회원가입 결과
     */
    @Transactional
    public ResponseDTO<?> signUpUser(SignUpDTO signUpDTO) {

        if (checkRequiredFields(signUpDTO)){
            return ResponseDTO.createErrorResponse(400, "요청값이 비어있습니다.");
        }

        if (existsUser(signUpDTO)) {
            return ResponseDTO.createErrorResponse(400, "이미 존재하는 이메일입니다.");
        }

        UserEntity user = UserEntity.fromDto(signUpDTO);
        user.setPassword(argon2PasswordEncoder.encode(signUpDTO.getPassword()));
        user.setActivated(true);
        user.setRole(RoleType.ROLE_USER);

        authRepository.save(user);

        return ResponseDTO.createSuccessResponse("회원가입이 완료되었습니다.", null);
    }

//    /**
//     * SNS 로그인 처리 (주석 처리됨)
//     * Firebase를 통한 소셜 로그인 처리
//     * 
//     * @param header 요청 헤더
//     * @param dto SNS 로그인 요청 DTO
//     * @param res HTTP 응답 객체
//     * @return 토큰 정보
//     * @throws FirebaseAuthException Firebase 인증 예외
//     */
//    public ResponseDTO<TokenDTO> joinSns(Map<String, Object> header, SnsSignInRequest dto, HttpServletResponse res) throws FirebaseAuthException {
//        String token = dto.getAccessToken();
//        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
//        String provider = dto.getType();
//        String email = decodedToken.getEmail();
//        String name = decodedToken.getName();
//
//        log.info("uid: {}, email: {}, name: {}", provider, email, name);
//
//        UserEntity user = findOrCreateUser(provider, email, name);
//
//        TokenDTO tokenDTO = tokenService.createTokenPair(user.getName(), user.getRole().toString(), email, user.getId());
//        tokenUtils.addRefreshTokenCookie(res, tokenDTO.getRefreshToken());
//
//        return ResponseDTO.<TokenDTO>builder()
//                .resultCode(200)
//                .resultMessage("소셜 가입/로그인 성공")
//                .data(tokenDTO)
//                .build();
//
//    }

    /**
     * 소셜 로그인 사용자 찾기 또는 생성
     * 기존 사용자가 있으면 업데이트하고, 없으면 새로 생성
     * 
     * @param provider 소셜 로그인 제공자 (google, apple 등)
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @return 사용자 엔티티
     */
    private UserEntity findOrCreateUser(String provider, String email, String name) {
        Optional<UserEntity> optionalUser = authRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            if (user.getProvider() == null || !provider.equals("google")) {
                user.setProvider("google");
            }

            if (user.getProvider() == null || !provider.equals("apple")) {
                user.setProvider("apple");
            }

            return user;
        } else {
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .name(name)
                    .role(RoleType.ROLE_USER)
                    .provider(provider)
                    .build();

            return authRepository.save(newUser);
        }
    }

    /**
     * Apple 로그인 처리
     * Apple ID를 통한 소셜 로그인 처리
     * 
     * @param header 요청 헤더
     * @param dto 사용자 정보 DTO
     * @return 토큰 정보
     */
    @Transactional
    public ResponseDTO<?> joinApple(Map<String, Object> header, UserDTO dto){
        UserEntity user = authRepository.findByEmail(dto.getEmail()).orElse(null);
        UserEntity savedUser;
        String provider = header.get("user-agent").toString();
        if (user == null){
            UserEntity newUser = UserEntity.builder()
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .role(RoleType.ROLE_USER)
                    .provider(provider)
                    .password(argon2PasswordEncoder.encode(dto.getPassword()))
                    .activated(true)
                    .build();
            savedUser = authRepository.save(newUser);
        } else {
            user.setProvider(provider);
            user.setEmail(dto.getEmail());
            savedUser = user;
        }
//        (String username, String role, String email, String userId)

        TokenDTO tokenDTO = tokenService.createTokenPair(savedUser.getId(), savedUser.getRole().toString(), savedUser.getEmail(), savedUser.getId());

        return ResponseDTO.<TokenDTO>builder()
                .resultCode(200)
                .resultMessage("소셜 가입/로그인 성공")
                .data(tokenDTO)
                .build();
    }

    /**
     * 필수 필드 검증
     * 회원가입 시 필요한 필드들이 모두 입력되었는지 확인
     * 
     * @param user 회원가입 정보
     * @return 필수 필드 누락 여부
     */
    private boolean checkRequiredFields(SignUpDTO user){
        return user.getEmail() == null || user.getPassword() == null || user.getName() == null;
    }

    /**
     * 사용자 존재 여부 확인
     * 이메일로 기존 사용자 존재 여부를 확인
     * 
     * @param user 회원가입 정보
     * @return 사용자 존재 여부
     */
    private boolean existsUser(SignUpDTO user){
        return authRepository.existsByEmail(user.getEmail());
    }

    /**
     * 사용자 정보 조회
     * 사용자 ID로 상세 정보를 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    public ResponseDTO<UserDTO> getUserInfo(String id) {
        UserEntity user = authRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        UserDTO userDTO = UserDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .image(user.getImage())
                .createdAt(user.getCreatedAt().toString())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .build();

        return ResponseDTO.<UserDTO>builder()
                .resultCode(200)
                .resultMessage("유저 정보 조회 성공")
                .data(userDTO)
                .build();
    }

    /**
     * 사용자 정보 수정
     * 사용자의 개인정보를 업데이트
     * 
     * @param dto 수정할 사용자 정보 DTO
     * @param id 사용자 ID
     * @return 수정 결과
     */
    @Transactional
    public ResponseDTO<?> updateUserInfo(UserUpdateDTO dto, String id) {
        UserEntity user = authRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());
        user.update(dto);

        return ResponseDTO.createSuccessResponse("유저 정보 수정 성공" , null);
    }

    /**
     * 멤버 정보 목록 조회
     * 여러 사용자 ID에 해당하는 사용자 정보를 조회
     * 
     * @param likedUserIds 조회할 사용자 ID 목록
     * @return 사용자 정보 목록
     */
    public List<UserDTO> getMemberInfoList(List<String> likedUserIds) {
        return authRepository.findByIdIn(likedUserIds).stream()
                .map( user -> UserDTO.builder()
                        .nickname(user.getNickname())
                        .image(user.getImage())
                        .email(user.getEmail())
                        .build()).toList();
    }
}
