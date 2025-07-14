package com.jakdang.labs.security.jwt.service;

import com.jakdang.labs.api.auth.dto.TokenDTO;
import com.jakdang.labs.api.auth.entity.UserToken;
import com.jakdang.labs.api.auth.repository.UserTokenRepository;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT 토큰 관리 서비스
 * 액세스 토큰과 리프레시 토큰의 생성, 갱신, 저장을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    /** 액세스 토큰 만료 시간 (밀리초) */
    @Value("${spring.jwt.access.expired}")
    private long accessTokenExpiration;

    /** 리프레시 토큰 만료 시간 (밀리초) */
    @Value("${spring.jwt.refresh.expired}")
    private long refreshTokenExpiration;

    private final JwtUtil jwtUtil;
    private final UserTokenRepository userTokenRepository;

    /**
     * 토큰 쌍 생성
     * 액세스 토큰과 리프레시 토큰을 생성하고 데이터베이스에 저장
     * 
     * @param username 사용자명
     * @param role 사용자 역할
     * @param email 사용자 이메일
     * @param userId 사용자 ID
     * @return 토큰 쌍 DTO
     */
    @Transactional
    public TokenDTO createTokenPair(String username, String role, String email, String userId) {
        String accessToken = createAccessToken(username, role, email, userId);
        String refreshToken = createRefreshToken(username, role, email, userId);

        saveTokenPair(accessToken, refreshToken);

        return new TokenDTO(accessToken, refreshToken);
    }

    /**
     * 토큰 쌍 갱신
     * 기존 리프레시 토큰을 검증하고 새로운 토큰 쌍을 생성
     * 
     * @param refreshToken 기존 리프레시 토큰
     * @return 새로운 토큰 쌍 DTO
     */
    @Transactional
    public TokenDTO refreshTokenPair(String refreshToken) {
        UserToken userToken = findRefreshEntity(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String userId = jwtUtil.getUserId(refreshToken);
        String email = jwtUtil.getUserEmail(refreshToken);

        String newAccessToken = createAccessToken(username, role, email, userId);
        String newRefreshToken = createRefreshToken(username, role, email, userId);

        updateRefreshEntity(userToken, newAccessToken, newRefreshToken);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    /**
     * 리프레시 토큰 엔티티 조회
     * 
     * @param refreshToken 리프레시 토큰
     * @return 사용자 토큰 엔티티
     */
    private UserToken findRefreshEntity(String refreshToken) {
        return userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다"));
    }

    /**
     * 액세스 토큰 생성
     * 
     * @param username 사용자명
     * @param role 사용자 역할
     * @param email 사용자 이메일
     * @param userId 사용자 ID
     * @return 액세스 토큰
     */
    private String createAccessToken(String username, String role, String email, String userId) {
        return jwtUtil.createJwt("access", username, role, email, userId, accessTokenExpiration);
    }

    /**
     * 리프레시 토큰 생성
     * 
     * @param username 사용자명
     * @param role 사용자 역할
     * @param email 사용자 이메일
     * @param userId 사용자 ID
     * @return 리프레시 토큰
     */
    private String createRefreshToken(String username, String role, String email, String userId) {
        return jwtUtil.createJwt("refresh", username, role, email, userId, refreshTokenExpiration);
    }

    /**
     * 
     * 리프레시 토큰을 데이터베이스에 저장
     * 
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    @Transactional // (**정은 수정 및 추가함 0710** - refreshToken 정보 추가 저장)
    protected void saveTokenPair(String accessToken, String refreshToken) {
        
        long expiresAt = Instant.now().getEpochSecond() + (refreshTokenExpiration / 1000); // 정은 추가

        // TODO 값 넣기
        UserToken refreshEntity = UserToken.builder()
                .userId(jwtUtil.getUserId(accessToken))
                .expiresAt(expiresAt) // 정은 추가
                .isRevoked(false) // 토큰이 폐기되었는지 - 예상) 로그아웃시 토큰 폐기할듯. 1(true)이 페기인듯? 기본값이 0(false)임. 
                .refreshToken(refreshToken)
                .build();

        userTokenRepository.save(refreshEntity);
    }

    /**
     * 리프레시 토큰 엔티티 업데이트
     * 기존 토큰 엔티티를 새로운 토큰으로 업데이트
     * 
     * @param oldEntity 기존 토큰 엔티티
     * @param newAccessToken 새로운 액세스 토큰
     * @param newRefreshToken 새로운 리프레시 토큰
     */
    @Transactional
    protected void updateRefreshEntity(UserToken oldEntity, String newAccessToken, String newRefreshToken) {
        oldEntity.setRefreshToken(newRefreshToken);
    }
}