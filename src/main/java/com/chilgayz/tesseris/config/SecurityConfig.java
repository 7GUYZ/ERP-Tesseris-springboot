package com.chilgayz.tesseris.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {


        @Bean
        SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception{
            http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                // 요청별 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/api/**").permitAll() //허용하는 URL 만 작성
                    .anyRequest().authenticated());


            return http.build();
        }

    @Bean
        CorsConfigurationSource corsConfigurationSource(){

            CorsConfiguration corsConfig = new CorsConfiguration();
            // 허용할 orign 설정, 머서드, 헤더, 인증
            corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
            corsConfig.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
            corsConfig.setAllowedHeaders(Arrays.asList("*"));
            corsConfig.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfig);

            return source;
            // webConfig.java 삭제하자
        }

        
}

