package com.jakdang.labs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Argon2Config {

    @Value("${security.password.argon2.memory:16384}")
    private int memory;

    @Value("${security.password.argon2.iterations:10}")
    private int iterations;

    @Value("${security.password.argon2.parallelism:1}")
    private int parallelism;

    @Bean
    public PasswordEncoder argon2PasswordEncoder() {
        // Spring Security의 기본 Argon2 설정 사용 (안전한 기본값)
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
} 