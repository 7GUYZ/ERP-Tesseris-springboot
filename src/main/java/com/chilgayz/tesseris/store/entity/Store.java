package com.chilgayz.tesseris.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "`Store`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_index")
    private Integer storeIndex;

    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "business_man_user_index")
    private Integer businessManUserIndex;

    @Column(name = "store_registration_num")
    private String storeRegistrationNum;

    @Column(name = "store_type_taxation")
    private String storeTypeTaxation;

    @Column(name = "store_corporate_name")
    private String storeCorporateName;

    @Column(name = "store_boss_name")
    private String storeBossName;

    @Column(name = "store_business_license_photo")
    private String storeBusinessLicensePhoto;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_category_index")
    private Integer storeCategoryIndex;

    @Column(name = "store_phone")
    private String storePhone;

    @Column(name = "store_site")
    private String storeSite;

    @Column(name = "store_zone_code")
    private String storeZoneCode;

    @Column(name = "store_address")
    private String storeAddress;

    @Column(name = "store_detail_address")
    private String storeDetailAddress;

    @Column(name = "store_sign_photo")
    private String storeSignPhoto;

    @Column(name = "store_pront_photo")
    private String storeFrontPhoto;

    @Column(name = "store_transaction_status")
    private Boolean storeTransactionStatus;

    @Column(name = "store_request_status_index")
    private Integer storeRequestStatusIndex;

    @Column(name = "store_qr_code")
    private String storeQrCode;

    @Column(name = "store_marketing_agree")
    private String storeMarketingAgree;

    @Column(name = "store_aed_agree")
    private String storeAedAgree;

    @Column(name = "store_low_agree")
    private String storeLowAgree;

    @Column(name = "store_pos1", columnDefinition = "TEXT")
    private String storePos1;

    @Column(name = "store_pos2", columnDefinition = "TEXT")
    private String storePos2;

    @Column(name = "store_registration_date")
    private LocalDateTime storeRegistrationDate;

    @Column(name = "store_create_date")
    private LocalDateTime storeCreateDate;

    @Column(name = "store_cancel_memo", columnDefinition = "VARCHAR(300)")
    private String storeCancelMemo;

    @Column(name = "store_holiday_status", columnDefinition = "VARCHAR(300)")
    private String storeHolidayStatus;

    @Column(name = "store_regular_closing_interval")
    private String storeRegularClosingInterval;

    @Column(name = "store_regular_closing_week")
    private String storeRegularClosingWeek;

    @Column(name = "store_temporary_closing_date")
    private String storeTemporaryClosingDate;

    @Column(name = "store_temporary_closing_comment")
    private String storeTemporaryClosingComment;

    @Column(name = "store_memo", columnDefinition = "TEXT")
    private String storeMemo;

    @Column(name = "store_save_value")
    private Integer storeSaveValue;

    @Column(name = "store_customer_flag")
    private Integer storeCustomerFlag;

    @Column(name = "store_limit")
    private Integer storeLimit;

}
