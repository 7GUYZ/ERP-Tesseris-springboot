package com.jakdang.labs.api.taekjun.signin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.taekjun.signin.dto.SignupRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.SignupResponseDTO;
import com.jakdang.labs.api.taekjun.signin.service.SignupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signin")
public class signin {
        
    private final SignupService signupService;
    
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO requestDTO) {
        SignupResponseDTO response = signupService.signup(requestDTO);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
