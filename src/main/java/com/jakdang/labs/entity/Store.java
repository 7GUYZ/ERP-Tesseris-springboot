package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_index")
    private Integer storeIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "business_man_user_index")
    private Integer businessManUserIndex;

    @Column(name = "store_registration_num", length = 90)
    private String storeRegistrationNum;

    @Column(name = "store_type_taxation", length = 90)
    private String storeTypeTaxation;

    @Column(name = "store_corporate_name", length = 90)
    private String storeCorporateName;

    @Column(name = "store_boss_name", length = 90)
    private String storeBossName;

    @Column(name = "store_business_license_photo", length = 300)
    private String storeBusinessLicensePhoto;

    @Column(name = "store_name", length = 90)
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_category_index")
    private StoreCategory storeCategory;

    @Column(name = "store_phone", length = 90)
    private String storePhone;

    @Column(name = "store_site", length = 150)
    private String storeSite;

    @Column(name = "store_zone_code", length = 30)
    private String storeZoneCode;

    @Column(name = "store_address", length = 90)
    private String storeAddress;

    @Column(name = "store_detail_address", length = 90)
    private String storeDetailAddress;

    @Column(name = "store_sign_photo", length = 300)
    private String storeSignPhoto;

    @Column(name = "store_pront_photo", length = 300)
    private String storeProntPhoto;

    @Column(name = "store_transaction_status")
    private Boolean storeTransactionStatus;

    @Column(name = "store_request_status_index")
    private Integer storeRequestStatusIndex;

    @Column(name = "store_qr_code", length = 90)
    private String storeQrCode;

    @Column(name = "store_marketing_agree", length = 30)
    private String storeMarketingAgree;

    @Column(name = "store_aed_agree", length = 30)
    private String storeAedAgree;

    @Column(name = "store_low_agree", length = 30)
    private String storeLowAgree;

    @Column(name = "store_pos1", columnDefinition = "TEXT")
    private String storePos1;

    @Column(name = "store_pos2", columnDefinition = "TEXT")
    private String storePos2;

    @Column(name = "store_registration_date")
    private LocalDateTime storeRegistrationDate;

    @Column(name = "store_create_date")
    private LocalDateTime storeCreateDate;

    @Column(name = "store_cancel_memo", length = 300)
    private String storeCancelMemo;

    @Column(name = "store_holiday_status", length = 50)
    private String storeHolidayStatus;

    @Column(name = "store_regular_closing_interval", length = 50)
    private String storeRegularClosingInterval;

    @Column(name = "store_regular_closing_week", length = 50)
    private String storeRegularClosingWeek;

    @Column(name = "store_temporary_closing_date", length = 300)
    private String storeTemporaryClosingDate;

    @Column(name = "store_temporary_closing_comment", length = 300)
    private String storeTemporaryClosingComment;

    @Column(name = "store_memo", columnDefinition = "TEXT")
    private String storeMemo;

    @Column(name = "store_save_value")
    private Integer storeSaveValue;

    @Column(name = "store_customer_flag")
    private Integer storeCustomerFlag;

    @Column(name = "store_limit")
    private Integer storeLimit;

    @Column(name = "franchise_fee")
    private Integer franchiseFee;

    @Column(name = "businessManUserIndex")
    private Integer businessManUserIndex2;

    @Column(name = "storeAddress", length = 255)
    private String storeAddress2;

    @Column(name = "storeAedAgree", length = 255)
    private String storeAedAgree2;

    @Column(name = "storeBossName", length = 255)
    private String storeBossName2;

    @Column(name = "storeBusinessLicensePhoto", length = 255)
    private String storeBusinessLicensePhoto2;

    @Column(name = "storeCancelMemo", length = 255)
    private String storeCancelMemo2;

    @Column(name = "storeCorporateName", length = 255)
    private String storeCorporateName2;

    @Column(name = "storeCreateDate")
    private LocalDateTime storeCreateDate2;

    @Column(name = "storeDetailAddress", length = 255)
    private String storeDetailAddress2;

    @Column(name = "storeHolidayStatus", length = 255)
    private String storeHolidayStatus2;

    @Column(name = "storeLowAgree", length = 255)
    private String storeLowAgree2;

    @Column(name = "storeMarketingAgree", length = 255)
    private String storeMarketingAgree2;

    @Column(name = "storeMemo", columnDefinition = "TEXT")
    private String storeMemo2;

    @Column(name = "storeName", length = 255)
    private String storeName2;

    @Column(name = "storePhone", length = 255)
    private String storePhone2;

    @Column(name = "storePos1", columnDefinition = "TEXT")
    private String storePos12;

    @Column(name = "storePos2", columnDefinition = "TEXT")
    private String storePos22;

    @Column(name = "storeProntPhoto", length = 255)
    private String storeProntPhoto2;

    @Column(name = "storeQrCode", length = 255)
    private String storeQrCode2;

    @Column(name = "storeRegistrationDate")
    private LocalDateTime storeRegistrationDate2;

    @Column(name = "storeRegistrationNum", length = 255)
    private String storeRegistrationNum2;

    @Column(name = "storeRegularClosingInterval", length = 255)
    private String storeRegularClosingInterval2;

    @Column(name = "storeRegularClosingWeek", length = 255)
    private String storeRegularClosingWeek2;

    @Column(name = "storeSignPhoto", length = 255)
    private String storeSignPhoto2;

    @Column(name = "storeSite", length = 255)
    private String storeSite2;

    @Column(name = "storeTemporaryClosingComment", length = 255)
    private String storeTemporaryClosingComment2;

    @Column(name = "storeTemporaryClosingDate", length = 255)
    private String storeTemporaryClosingDate2;

    @Column(name = "storeTransactionStatus")
    private Boolean storeTransactionStatus2;

    @Column(name = "storeTypeTaxation", length = 255)
    private String storeTypeTaxation2;

    @Column(name = "storeZoneCode", length = 255)
    private String storeZoneCode2;
} 