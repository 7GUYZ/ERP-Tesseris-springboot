package com.jakdang.labs.api.jiyun.mypage.controller;

import com.jakdang.labs.api.jiyun.mypage.dto.MypageUserInfoDto;
import com.jakdang.labs.api.jiyun.mypage.dto.SuggestionUserListDto;
import com.jakdang.labs.api.jiyun.mypage.service.MypageGeneralService;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/general/mypage")
@RequiredArgsConstructor
public class MypageGeneralController {
    private final MypageGeneralService mypageGeneralService;
    private final JwtUtil jwtUtil;
    @PersistenceContext
    private EntityManager em;

    @GetMapping("/getNickname")
    public ResponseEntity<MypageUserInfoDto> getNickname(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String id = jwtUtil.getUserId(token); // JWT에서 id(PK)를 추출
        return ResponseEntity.ok(mypageGeneralService.getNickname(id));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<SuggestionUserListDto>> getSuggestions(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        // userId로 UserTesseris의 userIndex 조회
        UserTesseris user = em.createQuery("SELECT u FROM UserTesseris u WHERE u.usersId.id = :id", UserTesseris.class)
                .setParameter("id", userId)
                .getSingleResult();
        List<SuggestionUserListDto> list = mypageGeneralService.getSuggestionList(user.getUserIndex());
        return ResponseEntity.ok(list);
    }
} 