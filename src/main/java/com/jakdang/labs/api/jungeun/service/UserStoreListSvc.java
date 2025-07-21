package com.jakdang.labs.api.jungeun.service;

import java.util.List;

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

//     public ResponseDTO<UserStoreDetailDTO> getStoreDetail(Integer store_index){
//         Object[] result = (Object[]) storeRepo.findStoreDetailByStoreIndex(store_index);
//         if (result == null) {
//             return ResponseDTO.createErrorResponse(-200, null);
//         }
//         UserStoreDetailDTO detailDTO = UserStoreDetailDTO.builder()
//             .storeIndex(result[0] == null ? null : ((Number) result[0]).intValue())
//             .storeName((String) result[1])
//             .storePhone((String) result[2])
//             .storeAddress((String) result[3])
//             .storeCategoryName((String) result[4])
//             .userCmUse(result[5] == null ? null : ((Number) result[5]).intValue())
//             .storeImage((String) result[6])
//             .storeBusinessState(result[7] == null ? null : ((Number) result[7]).intValue())
//             // 필요한 필드 추가
//             .build();
//         return ResponseDTO.createSuccessResponse("가맹점 상세정보 불러오기 성공", detailDTO);
//     }
}
