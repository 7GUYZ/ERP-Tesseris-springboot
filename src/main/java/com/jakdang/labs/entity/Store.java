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

    @Column(name = "storeAddress", length = 90)
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

    // 중복된 컬럼 제거

    // 중복된 컬럼들 제거
}