package com.jakdang.labs.security.jwt.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 유틸리티 클래스
 * JWT 토큰의 생성, 파싱, 검증을 담당하는 유틸리티
 */
@Component
public class JwtUtil {

    /** JWT 서명에 사용할 비밀키 */
    private final SecretKey secretKey;

    /**
     * JwtUtil 생성자
     * 설정에서 비밀키를 가져와서 SecretKey 객체를 생성
     * 
     * @param secret JWT 서명용 비밀키
     */
    public JwtUtil(@Value("${spring.jwt.secret.code}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 토큰에서 사용자명 추출
     * 
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * 토큰에서 사용자 역할 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /**
     * 토큰에서 카테고리 추출
     * 
     * @param token JWT 토큰
     * @return 토큰 카테고리 (access, refresh 등)
     */
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getUserEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    /**
     * 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 토큰 만료 여부 (true: 만료됨, false: 유효함)
     */
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * JWT 토큰 생성
     * 
     * @param category 토큰 카테고리 (access, refresh 등)
     * @param username 사용자명
     * @param role 사용자 역할
     * @param email 사용자 이메일
     * @param userId 사용자 ID
     * @param expiredMs 만료 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
    public String createJwt(String category, String username, String role, String email, String userId, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .claim("userId", userId)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}