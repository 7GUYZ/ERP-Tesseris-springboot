package com.jakdang.labs.api.jiyun.mypage.service;

import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserRole;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.jiyun.mypage.dto.MypageUserInfoDto;
import com.jakdang.labs.api.jiyun.mypage.dto.SuggestionUserListDto;
import com.jakdang.labs.api.jiyun.mypage.repository.MypageGeneralRepository;
import com.jakdang.labs.api.jiyun.mypage.repository.SuggestionUserkjyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageGeneralService {
    private final MypageGeneralRepository mypageGeneralRepository;
    private final SuggestionUserkjyRepository suggestionUserRepository;
    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public MypageUserInfoDto getNickname(String id) {
        UserTesseris user = mypageGeneralRepository.findByUsersId_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("UserTesseris not found"));
        MypageUserInfoDto dto = new MypageUserInfoDto();
        dto.setNickname(user.getUsersId().getNickname());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<SuggestionUserListDto> getSuggestionList(Integer userIndex) {
        List<SuggestionUser> suggestions = suggestionUserRepository.findByRecommendationUserIndexOrderByJoinDateDesc(userIndex);
        List<SuggestionUserListDto> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (SuggestionUser su : suggestions) {
            SuggestionUserListDto dto = new SuggestionUserListDto();
            // 추천인 정보
            UserTesseris suggestionUser = em.find(UserTesseris.class, su.getSuggestionUserIndex());
            if (suggestionUser != null) {
                UserEntity suggestionUserEntity = suggestionUser.getUsersId();
                dto.setSuggestionUserEmail(suggestionUserEntity.getEmail());
                dto.setSuggestionUserName(suggestionUserEntity.getName());
                // 역할
                UserRole suggestionRole = em.find(UserRole.class, suggestionUser.getUserRoleIndex());
                dto.setSuggestionUserRole(suggestionRole != null ? suggestionRole.getUserRoleKorNm() : null);
                // 가맹점명
                Store store = em.createQuery("SELECT s FROM Store s WHERE s.userIndex.userIndex = :userIndex", Store.class)
                        .setParameter("userIndex", su.getSuggestionUserIndex())
                        .setMaxResults(1)
                        .getResultStream().findFirst().orElse(null);
                dto.setSuggestionStoreName(store != null ? store.getStoreName() : null);
            }
            // 추천받은 사람 정보
            UserTesseris recommendationUser = em.find(UserTesseris.class, su.getRecommendationUserIndex());
            if (recommendationUser != null) {
                UserEntity recommendationUserEntity = recommendationUser.getUsersId();
                dto.setRecommendationUserEmail(recommendationUserEntity.getEmail());
                dto.setRecommendationUserName(recommendationUserEntity.getName());
                UserRole recommendationRole = em.find(UserRole.class, recommendationUser.getUserRoleIndex());
                dto.setRecommendationUserRole(recommendationRole != null ? recommendationRole.getUserRoleKorNm() : null);
            }
            dto.setJoinDate(su.getJoinDate().format(formatter));
            result.add(dto);
        }
        return result;
    }
} 