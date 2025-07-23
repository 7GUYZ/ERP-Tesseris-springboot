package com.jakdang.labs.api.jungeun.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.UserStoreCategoryDTO;
import com.jakdang.labs.api.jungeun.dto.UserStoreDetailDTO;
import com.jakdang.labs.api.jungeun.dto.UserStoreListDTO;
import com.jakdang.labs.api.jungeun.repository.StoreCategoryLjeRepo;
import com.jakdang.labs.api.jungeun.repository.StoreLjeRepo;
import com.jakdang.labs.entity.StoreCategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStoreListSvc {
    private final StoreCategoryLjeRepo categoryRepo;
    private final StoreLjeRepo storeRepo;

    public ResponseDTO<List<UserStoreCategoryDTO>> getStoreCategories(){
        List<StoreCategory> categories = categoryRepo.findAll();
        return ResponseDTO.createSuccessResponse("가맹점 항목 불러오기 성공", categories.stream()
                .map(entity -> UserStoreCategoryDTO.builder()
                        .categoryIndex(entity.getStoreCategoryIndex())
                        .categoryName(entity.getStoreCategoryName())
                        .build()).toList()
        );
    }

    public ResponseDTO<List<UserStoreListDTO>> getFilteredStoreList(Integer user_index, Integer store_category_index){
        List<Object[]> resultList = storeRepo.findFilteredStoreListWithUserIndex(user_index, store_category_index);

        List<UserStoreListDTO> dtoList = resultList.stream().map(arr -> 
        UserStoreListDTO.builder()
            .storeIndex(arr[0] == null ? null : ((Number) arr[0]).intValue())
            .storeName((String) arr[1])
            .storePhone((String) arr[2])
            .storeAddress((String) arr[3])
            .storeCategoryName((String) arr[4])
            .userCmUse(arr[5] == null ? null : ((Number) arr[5]).intValue())
            .storeImage((String) arr[6])
            .storeBusinessState(arr[7] == null ? null : ((Number) arr[7]).intValue())
            .build()
        ).toList();

        return ResponseDTO.createSuccessResponse("선택 항목에 따른 가맹점 리스트 불러오기 성공", dtoList);
    }

    public ResponseDTO<?> getStoreDetail(Integer store_index){
        Object[] result = (Object[]) storeRepo.findStoreDetailByStoreIndex(store_index);
        if (result == null) {
            return ResponseDTO.createErrorResponse(500, "가맹점 상세정보 불러오기 실패");
        }
        
        // 이미지 문자열을 배열로 변환
        List<String> storeImages = new ArrayList<>();
        if (result[9] != null) {
            String imagesString = result[9].toString();
            if (!imagesString.isEmpty()) {
                storeImages = Arrays.asList(imagesString.split(","));
            }
        }
        
        UserStoreDetailDTO detailDTO = UserStoreDetailDTO.builder()
            .storeIndex(result[0] == null ? null : ((Number) result[0]).intValue()) // INT
            .storeName(result[1] == null ? null : result[1].toString()) // VARCHAR
            .storePhone(result[2] == null ? null : result[2].toString()) // VARCHAR or BIGINT
            .storeAddress(result[3] == null ? null : result[3].toString())
            .storeDetailAddress(result[4] == null ? null : result[4].toString())
            .storeSite(result[5] == null ? null : result[5].toString())
            .storeMemo(result[6] == null ? null : result[6].toString())
            .storeCategoryName(result[7] == null ? null : result[7].toString())
            .userCmUse(result[8] == null ? null : ((Number) result[8]).intValue()) // 연산 결과
            .storeImages(storeImages) // 이미지 배열
            .storeBusinessState(result[10] == null ? null : ((Number) result[10]).intValue()) // IF문 결과
            .storeBusinessDate(result[11] == null ? null : result[11].toString())
            .storeBusinessHour(result[12] == null ? null : result[12].toString())
            .storeRestHour(result[13] == null ? null : result[13].toString())
            .build();
        return ResponseDTO.createSuccessResponse("가맹점 상세정보 불러오기 성공", detailDTO);
    }
}
