package com.jakdang.labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableFeignClients
public class NewBackendApplication {

    public static void main(String[] args) {
        // .env 파일 자동 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        SpringApplication.run(NewBackendApplication.class, args);
    }

}
