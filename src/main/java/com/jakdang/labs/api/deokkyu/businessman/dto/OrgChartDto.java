package com.jakdang.labs.api.deokkyu.businessman.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgChartDto { // 사업자 조직도로 보낼 DTO
    private String businessManId; // user_index로 user_tesseris테이블 가서 users_id얻기
    private String businessManName; // 위에서 얻은 users_id로 users 테이블가서 name얻기
    private String businessGradeName; // business_grade_index로 business_grade 테이블에서 business_grade_name 얻기
    private String businessAreaName; // business_area_index로 business_area 테이블에서 business_area_name 얻기
    private String bossUserIndex; // boss_user_index로 user_tesseris테이블에서 users_id 얻기
    private Integer currentTotalStore; // user_index로 store테이블에서 business_man_user_index 와 일치하는 칼럼의 수를 센다
    private Integer totalStore; // 하위 사업자들의 currentTotalStore를 더한 값
    private Double allowance; // temporary_store_detail 테이블의 temporary_store_cm_value 값들을 모두 더한 값
}
