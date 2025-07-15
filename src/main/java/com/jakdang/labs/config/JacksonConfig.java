//package com.jakdang.labs.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
//@Configuration
//public class JacksonConfig {
//
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
//        ObjectMapper objectMapper = builder.build();
//
//        // OAuth2 모듈 등록
//        objectMapper.registerModule(new OAuth2JacksonModule());
//
//        // 추가 설정
//        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//
//        return objectMapper;
//    }
//}