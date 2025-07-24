package com.jakdang.labs.api.dabin.CmsMemberRecommendation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto.MemberRecommendationSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto.MemberRecommendationSearchResponseDto;
import com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto.UserRoleDto;
import com.jakdang.labs.api.dabin.CmsMemberRecommendation.service.CmsMemberRecommendationService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/member-recommendation")
@RequiredArgsConstructor
public class CmsMemberRecommendationController {

    private final CmsMemberRecommendationService memberRecommendationService;

    @PostMapping("/search")
    public List<MemberRecommendationSearchResponseDto> searchMemberRecommendations(@RequestBody MemberRecommendationSearchRequestDto dto) {
        return memberRecommendationService.searchMemberRecommendations(dto);
    }

    @GetMapping("/user-roles")
    public List<UserRoleDto> getUserRoleList() {
        return memberRecommendationService.getUserRoleList();
    }
} 