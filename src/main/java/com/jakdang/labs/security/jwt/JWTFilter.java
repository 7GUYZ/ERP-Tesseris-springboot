package com.jakdang.labs.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.config.SecurityConfig;
import com.jakdang.labs.exceptions.JwtExceptionCode;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.exceptions.handler.CustomException;
import com.jakdang.labs.exceptions.handler.JwtException;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.security.jwt.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * JWT 인증 필터
 * 요청에서 JWT 토큰을 추출하고 검증하여 사용자 인증을 처리하는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 필터 내부 처리 로직
     * 요청에서 액세스 토큰을 추출하고 검증하여 인증을 처리
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws IOException IO 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String accessToken = extractAccessToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Access token 확인: {}", accessToken);

        try {
            if (tokenUtils.isAccessTokenValid(accessToken)) {
                Authentication authentication = createAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("사용자 인증 성공: {}", authentication.getName());
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰");
            handleJwtException(response, JwtExceptionCode.EXPIRED_JWT_TOKEN);
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰}");
            handleJwtException(response, JwtExceptionCode.INVALID_JWT_TOKEN);
        } catch (SignatureException e) {
            log.error("JWT 서명 위조가 의심됩니다.");
            handleJwtException(response, JwtExceptionCode.SIGNATURE_VALIDATION_ERROR);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰  EX) 유저 롤값 정의");
            handleJwtException(response, JwtExceptionCode.INVALID_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰 처리 중 오류");
            handleJwtException(response, JwtExceptionCode.INVALID_JWT_TOKEN);
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 예상치 못한 오류");
            handleJwtException(response, JwtExceptionCode.INVALID_JWT_TOKEN);
        }
    }

    /**
     * JWT 예외 처리
     * JWT 관련 예외를 HTTP 응답으로 변환하여 반환
     * 
     * @param response HTTP 응답
     * @param exceptionCode JWT 예외 코드
     * @throws IOException IO 예외
     */
    private void handleJwtException(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        objectMapper.writeValue(response.getOutputStream(),
                Map.of("error", exceptionCode.getMessage(), "message", exceptionCode.getMessage()));

        throw new JwtException(exceptionCode);
    }

    /**
     * 요청에서 액세스 토큰 추출
     * Authorization 헤더에서 Bearer 토큰을 추출
     * 
     * @param request HTTP 요청
     * @return 액세스 토큰 (없으면 null)
     */
    private String extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7))
                .filter(token -> !token.isEmpty() && !token.equals("undefined"))
                .orElse(null);
    }

    /**
     * 토큰으로부터 인증 객체 생성
     * JWT 토큰의 정보를 바탕으로 Authentication 객체를 생성
     * 
     * @param accessToken 액세스 토큰
     * @return 인증 객체
     */
    private Authentication createAuthentication(String accessToken) {
        CustomUserDetails userDetails = buildUserDetails(accessToken);
        log.info(userDetails.toString());
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    /**
     * 토큰으로부터 사용자 상세 정보 생성
     * JWT 토큰의 클레임을 파싱하여 CustomUserDetails 객체를 생성
     * 
     * @param accessToken 액세스 토큰
     * @return 사용자 상세 정보
     */
    private CustomUserDetails buildUserDetails(String accessToken) {
        return new CustomUserDetails(
                UserEntity.builder()
                        .id(jwtUtil.getUserId(accessToken))
                        .name(jwtUtil.getUsername(accessToken))
                        .email(jwtUtil.getUserEmail(accessToken))
                        .role(RoleType.valueOf(jwtUtil.getRole(accessToken)))
                        .build()
        );
    }

    /**
     * 필터 적용 여부 결정
     * 공개 URL이나 Swagger URL에는 필터를 적용하지 않음
     * 
     * @param request HTTP 요청
     * @return 필터 적용 여부 (true: 적용하지 않음, false: 적용함)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // SecurityConfig에 정의된 PUBLIC_URLS 패턴 활용
        return Arrays.stream(SecurityConfig.PUBLIC_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path))
                || Arrays.stream(SecurityConfig.SWAGGER_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}