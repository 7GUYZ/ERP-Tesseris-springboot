package com.jakdang.labs.api.taekjun.signin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.taekjun.signin.dto.ReferralListDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralResponseDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchResultDTO;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/referral")
public class ReferralController {
    
    private final ReferralService referralService;
    
    /**
     * 추천인 관계 생성
     */
    @PostMapping("/create")
    public ResponseEntity<ReferralResponseDTO> createReferralRelation(@RequestBody ReferralRequestDTO requestDTO) {
        log.info("추천인 관계 생성 요청: {}", requestDTO);
        
        ReferralResponseDTO response = referralService.createReferralRelation(requestDTO);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 사용자의 추천인 목록 조회
     */
    @GetMapping("/list/{userIndex}")
    public ResponseEntity<List<ReferralListDTO>> getReferralList(@PathVariable Integer userIndex) {
        log.info("추천인 목록 조회 요청: userIndex={}", userIndex);
        
        List<ReferralListDTO> referralList = referralService.getReferralList(userIndex);
        
        return ResponseEntity.ok(referralList);
    }
    
    /**
     * 사용자의 추천인 수 조회
     */
    @GetMapping("/count/{userIndex}")
    public ResponseEntity<Integer> getReferralCount(@PathVariable Integer userIndex) {
        log.info("추천인 수 조회 요청: userIndex={}", userIndex);
        
        int count = referralService.getReferralCount(userIndex);
        
        return ResponseEntity.ok(count);
    }
    
    /**
     * 추천인 코드 유효성 검사
     */
    @GetMapping("/validate/{referralCode}")
    public ResponseEntity<Boolean> validateReferralCode(@PathVariable String referralCode) {
        log.info("추천인 코드 유효성 검사: referralCode={}", referralCode);
        
        boolean isValid = referralService.findUserByReferralCode(referralCode).isPresent();
        
        return ResponseEntity.ok(isValid);
    }
    
    /**
     * 아이디로 사용자 검색 (이메일 또는 닉네임)
     */
    @GetMapping("/search/{identifier}")
    public ResponseEntity<UserSearchResultDTO> searchUserByIdentifier(@PathVariable String identifier) {
        log.info("사용자 검색 요청: identifier={}", identifier);
        
        UserSearchResultDTO result = referralService.searchUserByIdentifier(identifier);
        
        if (result.isFound()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 검색 타입에 따라 사용자 검색
     */
    @PostMapping("/search")
    public ResponseEntity<UserSearchResultDTO> searchUserByType(@RequestBody UserSearchDTO searchDTO) {
        log.info("사용자 검색 요청: type={}, value={}", searchDTO.getSearchType(), searchDTO.getSearchValue());
        
        UserSearchResultDTO result = referralService.searchUserByType(searchDTO);
        
        if (result.isFound()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 