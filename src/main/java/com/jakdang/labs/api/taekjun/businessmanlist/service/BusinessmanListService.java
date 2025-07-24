package com.jakdang.labs.api.taekjun.businessmanlist.service;

import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListResponseDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListSearchDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.BusinessmanListRepository;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserBank;
import com.jakdang.labs.api.taekjun.user_list.repository.UserListJtjRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessmanListService {
    private final BusinessmanListRepository businessmanListRepository;
    private final UserListJtjRepo userTesserisRepository;

    public List<BusinessmanListResponseDTO> searchBusinessmanList(BusinessmanListSearchDTO searchDTO) {
        List<BusinessMan> list = businessmanListRepository.searchBusinessManList(
                searchDTO.getEmail(),
                searchDTO.getUserName(),
                searchDTO.getUserPhone(),
                searchDTO.getBusinessGradeIndex(),
                searchDTO.getBossEmail(),
                searchDTO.getBusinessAreaName(),
                searchDTO.getBusinessManDistributionFlag()
        );
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private BusinessmanListResponseDTO toDto(BusinessMan bm) {
        UserTesseris user = bm.getUserIndex();
        UserBank userBank = user.getUserBank();
        BusinessmanListResponseDTO dto = new BusinessmanListResponseDTO();
        dto.setUserIndex(user.getUserIndex());
        dto.setEmail(user.getUsersId() != null ? user.getUsersId().getEmail() : null); // 이메일
        dto.setUserName(user.getUsersId() != null ? user.getUsersId().getName() : null);
        dto.setUserPhone(user.getUsersId() != null ? user.getUsersId().getPhone() : null);
        // bossEmail 조회
        String bossEmail = null;
        if (bm.getBossUserIndex() != null) {
            Optional<UserTesseris> bossUser = userTesserisRepository.findById(bm.getBossUserIndex());
            if (bossUser.isPresent() && bossUser.get().getUsersId() != null) {
                bossEmail = bossUser.get().getUsersId().getEmail();
            }
        }
        dto.setBossEmail(bossEmail);
        dto.setBusinessGradeName(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeName() : null);
        dto.setBusinessManDistributionFlag(bm.getBusinessManDistributionFlag() != null && bm.getBusinessManDistributionFlag() ? "정상" : "정지");
        dto.setUserBankName(userBank != null ? userBank.getUserBankName() : null);
        dto.setUserBankNumber(user.getUserBankNumber());
        dto.setUserBankHolder(user.getUserBankHolder());
        dto.setBusinessAreaName(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaName() : null);
        dto.setBusinessGradeIndex(bm.getBusinessGrade() != null ? bm.getBusinessGrade().getBusinessGradeIndex() : null);
        dto.setBusinessAreaIndex(bm.getBusinessArea() != null ? bm.getBusinessArea().getBusinessAreaIndex() : null);
        return dto;
    }
} 