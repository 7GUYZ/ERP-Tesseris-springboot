package com.jakdang.labs.api.taekjun.user_list.service;

import com.jakdang.labs.api.taekjun.user_list.Dto.UserListResponseDTO;
import com.jakdang.labs.api.taekjun.user_list.repository.UserListJtjRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserListService {
    private final UserListJtjRepo userListJtjRepo;

    public List<UserListResponseDTO> getUserList() {
        List<Object[]> rawList = userListJtjRepo.findUserListRaw();
        return rawList.stream()
                .map(this::mapToDto)
                .filter(dto -> dto.getStoreName() != null) // storeName이 null인 경우 제외
                .collect(Collectors.toList());
    }

    private UserListResponseDTO mapToDto(Object[] row) {
        UserListResponseDTO dto = new UserListResponseDTO();
        dto.setUserIndex((Integer) row[0]);
        dto.setName((String) row[1]);
        dto.setEmail((String) row[2]);
        dto.setPhone((String) row[3]);
        dto.setNickname((String) row[4]);
        dto.setBirthday(row[5] != null ? row[5].toString() : null);
        dto.setGender((String) row[6]);
        dto.setBankName((String) row[7]);
        dto.setBankNumber((String) row[8]);
        dto.setBankHolder((String) row[9]);
        dto.setStoreName((String) row[10]);
        dto.setRecommenderName((String) row[11]);
        dto.setRecommenderId(row[12] != null ? Integer.parseInt(row[12].toString()) : null);
        dto.setSuggestionJoinDate(row[13] != null ? row[13].toString() : null);
        dto.setCmBalance(row[14] != null ? Integer.parseInt(row[14].toString()) : 0);
        dto.setRegistrationDate(row[15] != null ? row[15].toString() : null);
        return dto;
    }
} 