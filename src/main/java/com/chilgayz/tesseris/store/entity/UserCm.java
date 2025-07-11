package com.chilgayz.tesseris.store.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "`User_Cm`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_index")
    private Integer userCmIndex;

    @Column(name = "user_cm_deposit")
    private Integer userCmDeposit;

    @Column(name = "user_cm_withdrawal")
    private Integer userCmWithdrawal;

    @Column(name = "user_cmp_init")
    private Integer userCmpInit;

    @Column(name = "user_cm_pincode")
    private String userCmPincode;

    // 필드 생략함
}
