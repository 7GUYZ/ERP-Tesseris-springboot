package com.jakdang.labs.api.taekjun.businessmanlist.service;

import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListResponseDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListSearchDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.BusinessmanListJtjRepo;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.UserBankJtjRepo;
import com.jakdang.labs.api.taekjun.businessmanlist.repository.BusinessAreaJtjRepo;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserBank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanCreateRequestDTO;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class BusinessmanListService {
    private final BusinessmanListJtjRepo businessmanListRepository;
    private final UserBankJtjRepo userBankJtjRepo;
    private final BusinessAreaJtjRepo businessAreaJtjRepo;

    public List<BusinessmanListResponseDTO> searchBusinessmanList(BusinessmanListSearchDTO searchDTO) {
        List<BusinessMan> list = businessmanListRepository.searchBusinessManList(
                searchDTO.getEmail(),
                searchDTO.getUserName(),
                searchDTO.getUserPhone(),
                searchDTO.getBusinessGradeName(),
                searchDTO.getBossEmail(),
                searchDTO.getBusinessAreaName(),
                searchDTO.getBusinessAreaLevel(),
                searchDTO.getBusinessManDistributionFlag()
        );
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // createBusinessman, toDto 등 나머지 메서드는 실제 필요한 Repository/Service 구조에 맞게 추가 구현 필요
} 