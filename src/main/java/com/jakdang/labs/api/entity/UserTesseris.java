package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.jakdang.labs.api.auth.entity.UserEntity;

@Entity
@Table(name = "user_tesseris")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTesseris {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_index")
    private Integer userIndex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
    private UserEntity usersId;

    @Column(name = "user_birthday")
    private LocalDate userBirthday;

    @ManyToOne
    @JoinColumn(name = "user_gender_index")
    private UserGender userGender;

    @Column(name = "user_role_index", nullable = false)
    private Integer userRoleIndex;

    @ManyToOne
    @JoinColumn(name = "user_bank_index")
    private UserBank userBank;

    @Column(name = "user_bank_number", length = 90)
    private String userBankNumber;

    @Column(name = "user_bank_holder", length = 90)
    private String userBankHolder;

    @Column(name = "user_zone_code", length = 90)
    private String userZoneCode;

    @Column(name = "user_address", length = 90)
    private String userAddress;

    @Column(name = "user_detail_address", length = 90)
    private String userDetailAddress;

    @Column(name = "user_profile")
    private byte[] userProfile;

    @Column(name = "user_qr_code", length = 200)
    private String userQrCode;

    @Column(name = "user_amount")
    private Integer userAmount;

    @Column(name = "user_transaction_status", length = 50)
    private String userTransactionStatus;

    @Column(name = "user_login_status", length = 100)
    private String userLoginStatus;

    @Column(name = "user_cm_log_index")
    private Integer userCmLogIndex;

    @Column(name = "store_main_user_index")
    private Integer storeMainUserIndex;

    @Column(name = "user_key", length = 255)
    private String userKey;

    @Column(name = "user_coupon_index", columnDefinition = "LONGTEXT")
    private String userCouponIndex;

    @Column(name = "coupon_amount")
    private Integer couponAmount;

    @Column(name = "user_index_insert")
    private Integer userIndexInsert;

    @Column(name = "user_marketing_checked", length = 50)
    private String userMarketingChecked;

    @Column(name = "user_advertisement_checked", length = 50)
    private String userAdvertisementChecked;

    @Column(name = "user_position_checked", length = 50)
    private String userPositionChecked;

    @Column(name = "user_login_status2")
    private Integer userLoginStatus2;

    @Column(name = "user_upgrade", length = 50)
    private String userUpgrade;

    @Column(name = "user_jumin", length = 50)
    private String userJumin;

    @Column(name = "signup_path", length = 20)
    private String signupPath;

    @Column(name = "user_vip", length = 20)
    private String userVip;
}