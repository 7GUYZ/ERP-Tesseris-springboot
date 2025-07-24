package com.jakdang.labs.api.jihun.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 엑셀 다운로드 성능 최적화를 위한 DTO
 * 
 * 주요 최적화 포인트:
 * 1. Object[] 배열을 직접 매핑하여 변환 오버헤드 최소화
 * 2. 필요한 데이터만 포함하여 메모리 사용량 최소화
 * 3. 불필요한 엔티티 변환 과정 제거
 * 4. 대용량 데이터 처리에 최적화된 구조
 */
public class ExcelDownloadDto {

    /**
     * 회원 자산 내역 엑셀 다운로드용 DTO
     * 
     * 성능 최적화 내용:
     * - Object[] 배열을 직접 매핑하여 변환 속도 향상
     * - EntityNotFoundException 방지를 위한 안전한 데이터 처리
     * - 메모리 효율적인 데이터 구조
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberAccountExcelData {
        private Long userCmLogIndex;
        private Integer userCmLogValue;
        private String userCmLogReason;
        private String userCmLogCreateTime;
        private Integer userCouponValue;
        private String transactionTypeName;
        private String eventTriggerUserEmail;
        private String eventTriggerUserRole;
        private String eventPartyUserEmail;
        private String eventPartyUserName;
        private String eventPartyUserRole;

        /**
         * Object[] 배열을 MemberAccountExcelData로 변환
         * 
         * 성능 최적화 내용:
         * - 직접 배열 인덱스 접근으로 변환 속도 향상
         * - null 체크를 통한 안전한 데이터 처리
         * - 불필요한 엔티티 변환 과정 제거
         * 
         * @param row Object[] 배열 (Repository에서 반환된 데이터)
         * @return 변환된 MemberAccountExcelData 객체
         */
        public static MemberAccountExcelData fromObjectArray(Object[] row) {
            return MemberAccountExcelData.builder()
                .userCmLogIndex(getLongValue(row, 0))
                .userCmLogValue(getIntegerValue(row, 1))
                .userCmLogReason(getStringValue(row, 2))
                .userCmLogCreateTime(getStringValue(row, 3))
                .userCouponValue(getIntegerValue(row, 4))
                .transactionTypeName(getStringValue(row, 5))
                .eventTriggerUserEmail(getStringValue(row, 6))
                .eventTriggerUserRole(getStringValue(row, 7))
                .eventPartyUserEmail(getStringValue(row, 8))
                .eventPartyUserName(getStringValue(row, 9))
                .eventPartyUserRole(getStringValue(row, 10))
                .build();
        }
    }

    /**
     * 회원 자산 현황 엑셀 다운로드용 DTO
     * 
     * 성능 최적화 내용:
     * - Object[] 배열을 직접 매핑하여 변환 속도 향상
     * - 필요한 데이터만 포함하여 메모리 효율성 증대
     * - 안전한 데이터 변환 처리
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberAssetDetailsExcelData {
        private Long userIndex;
        private String usersId;
        private Integer userRoleIndex;
        private String userName;
        private String userPhone;
        private Integer userCmCurrent;
        private Integer userCmpCurrent;
        private Integer userCashCurrent;
        private String storeName;

        /**
         * Object[] 배열을 MemberAssetDetailsExcelData로 변환
         * 
         * 성능 최적화 내용:
         * - 직접 배열 인덱스 접근으로 변환 속도 향상
         * - null 체크를 통한 안전한 데이터 처리
         * - 불필요한 엔티티 변환 과정 제거
         * 
         * @param row Object[] 배열 (Repository에서 반환된 데이터)
         * @return 변환된 MemberAssetDetailsExcelData 객체
         */
        public static MemberAssetDetailsExcelData fromObjectArray(Object[] row) {
            return MemberAssetDetailsExcelData.builder()
                .userIndex(getLongValue(row, 0))
                .usersId(getStringValue(row, 1))
                .userRoleIndex(getIntegerValue(row, 2))
                .userName(getStringValue(row, 3))
                .userPhone(getStringValue(row, 4))
                .userCmCurrent(getIntegerValue(row, 6))
                .userCmpCurrent(getIntegerValue(row, 7))
                .userCashCurrent(getIntegerValue(row, 8))
                .storeName(getStringValue(row, 5))
                .build();
        }
    }

    /**
     * 안전한 Long 값 추출
     * 
     * @param row Object[] 배열
     * @param index 배열 인덱스
     * @return Long 값 또는 null
     */
    private static Long getLongValue(Object[] row, int index) {
        return row != null && row.length > index && row[index] != null ? 
            Long.valueOf(row[index].toString()) : null;
    }

    /**
     * 안전한 Integer 값 추출
     * 
     * @param row Object[] 배열
     * @param index 배열 인덱스
     * @return Integer 값 또는 null
     */
    private static Integer getIntegerValue(Object[] row, int index) {
        return row != null && row.length > index && row[index] != null ? 
            Integer.valueOf(row[index].toString()) : null;
    }

    /**
     * 안전한 String 값 추출
     * 
     * @param row Object[] 배열
     * @param index 배열 인덱스
     * @return String 값 또는 빈 문자열
     */
    private static String getStringValue(Object[] row, int index) {
        return row != null && row.length > index && row[index] != null ? 
            row[index].toString() : "";
    }
} 