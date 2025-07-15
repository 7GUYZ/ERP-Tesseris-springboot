package com.jakdang.labs.api.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.jakdang.labs.aop.RunningTime;
import com.jakdang.labs.api.auth.dto.*;
import com.jakdang.labs.api.auth.service.AuthService;
import com.jakdang.labs.api.auth.service.RefreshTokenService;
import com.jakdang.labs.api.common.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;  
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 * 사용자 회원가입, 로그인, 토큰 갱신, 사용자 정보 조회/수정 등의 기능을 제공
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    /**
     * 액세스 토큰 갱신 API
     * 쿠키에서 리프레시 토큰을 확인하여 새로운 액세스 토큰을 발급
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 갱신된 액세스 토큰
     */
    @PostMapping("/refresh")
    @RunningTime
    public ResponseEntity<ResponseDTO<?>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = refreshTokenService.refreshTokens(request.getCookies(), response);
        response.setHeader("Authorization", "Bearer " + accessToken);
        return ResponseEntity.ok().body(ResponseDTO.createSuccessResponse("리프레시 토큰 갱신완료", accessToken));
    }

    /**
     * 사용자 회원가입 API
     * 새로운 사용자를 시스템에 등록
     * 
     * @param signUpDTO 회원가입 정보 DTO
     * @return 회원가입 결과
     */
    @PostMapping(value = "/signUp")
    @RunningTime
    public ResponseEntity<ResponseDTO<?>> signUp(@RequestBody SignUpDTO signUpDTO) {
        if ( signUpDTO == null ) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                            .resultCode(400)
                            .resultMessage("요청값이 비어있습니다.")
                    .build());
        }

        return ResponseEntity.ok().body(authService.signUpUser(signUpDTO));
    }

//    /**
//     * SNS 회원가입 API (주석 처리됨)
//     * Firebase를 통한 SNS 로그인 처리
//     * 
//     * @param header 요청 헤더
//     * @param dto SNS 로그인 요청 DTO
//     * @param res HTTP 응답 객체
//     * @return SNS 회원가입 결과
//     * @throws FirebaseAuthException Firebase 인증 예외
//     */
//    @PostMapping(value = "/join-sns")
//    @RunningTime
//    public ResponseEntity<ResponseDTO<?>> joinSns(@RequestHeader Map<String, Object> header,
//                                                  @RequestBody SnsSignInRequest dto, HttpServletResponse res) throws FirebaseAuthException {
//        if ( dto == null ){
//            return ResponseEntity.badRequest().body(
//                    ResponseDTO.createErrorResponse(-200, "요청값이 비어있습니다."));
//        }
//
//        return ResponseEntity.ok().body(authService.joinSns(header, dto, res));
//    }

    /**
     * Apple 로그인 회원가입 API
     * Apple ID를 통한 소셜 로그인 처리
     * 
     * @param header 요청 헤더
     * @param dto 사용자 정보 DTO
     * @return Apple 로그인 회원가입 결과
     */
    @PostMapping(value = "/join-sns2")
    @RunningTime
    public ResponseEntity<ResponseDTO<?>> joinApple(@RequestHeader Map<String, Object> header,
                                                  @RequestBody UserDTO dto){
        if ( dto == null ){
            return ResponseEntity.badRequest().body(
                    ResponseDTO.createErrorResponse(-200, "요청값이 비어있습니다."));
        }

        return ResponseEntity.ok().body(authService.joinApple(header, dto));
    }

    /**
     * 사용자 정보 조회 API
     * 인증된 사용자의 상세 정보를 조회
     * 
     * @param details 인증된 사용자 정보
     * @return 사용자 정보
     */
    @GetMapping(value = "/get-info")
    @RunningTime
    public ResponseEntity<ResponseDTO<?>> getUserInfo(@AuthenticationPrincipal CustomUserDetails details){
        if ( details == null ){
            return ResponseEntity.badRequest().body(
                    ResponseDTO.createErrorResponse(-200, "인증/인가되지 않은 사용자"));
        }
        return ResponseEntity.ok().body(authService.getUserInfo(details.getUserId()));
    }

    /**
     * 사용자 정보 수정 API
     * 사용자의 개인정보를 업데이트
     * 
     * @param dto 수정할 사용자 정보 DTO
     * @param id 사용자 ID
     * @param details 인증된 사용자 정보
     * @return 사용자 정보 수정 결과
     */
    @PutMapping(value = "/info/{id}")
    @RunningTime
    public ResponseEntity<ResponseDTO<?>> updateUserInfo(@RequestBody UserUpdateDTO dto,
                                                         @PathVariable("id") String id,
                                                         @AuthenticationPrincipal CustomUserDetails details){
        if ( dto == null){
            return ResponseEntity.badRequest().body(
                    ResponseDTO.createErrorResponse(-200, "요청값이 비어있습니다."));
        }

        return ResponseEntity.ok().body(authService.updateUserInfo(dto, id));
    }
}
