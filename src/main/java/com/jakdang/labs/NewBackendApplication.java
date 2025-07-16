package com.jakdang.labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NewBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewBackendApplication.class, args);
    }

}
