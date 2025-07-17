package com.jakdang.labs.api.dabin.controller.CmsMemberRecommendation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController.MemberRecommendationSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController.MemberRecommendationSearchResponseDto;
import com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController.UserRoleDto;
import com.jakdang.labs.api.dabin.service.CmsMemberRecommendation.CmsMemberRecommendationService;

@RestController
@RequestMapping("/api/member-recommendation")
public class CmsMemberRecommendationController {
    @Autowired
    private CmsMemberRecommendationService memberRecommendationService;

    @PostMapping("/search")
    public List<MemberRecommendationSearchResponseDto> searchMemberRecommendations(@RequestBody MemberRecommendationSearchRequestDto dto) {
        return memberRecommendationService.searchMemberRecommendations(dto);
    }

    @GetMapping("/user-roles")
    public List<UserRoleDto> getUserRoleList() {
        return memberRecommendationService.getUserRoleList();
    }
} 