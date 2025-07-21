package com.jakdang.labs.api.taekjun.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSearchResponseDto {
    
    private List<AddressDocument> documents;
    private AddressMeta meta;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDocument {
        private String address_name;
        private String address_type;
        private String x;
        private String y;
        private Address address;
        private Address road_address;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_3depth_h_name;
        private String h_code;
        private String b_code;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;
        private String x;
        private String y;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressMeta {
        private boolean is_end;
        private int pageable_count;
        private int total_count;
        private int same_name;
        private RegionInfo region_info;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionInfo {
        private String keyword;
        private String selected_region;
    }
} 