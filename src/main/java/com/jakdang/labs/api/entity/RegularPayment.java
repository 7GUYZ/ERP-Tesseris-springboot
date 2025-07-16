package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "regular_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegularPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "resultCd", length = 10)
    private String resultCd;

    @Column(name = "resultMsg", length = 100)
    private String resultMsg;

    @Column(name = "advanceMsg", length = 100)
    private String advanceMsg;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "authCd", length = 50)
    private String authCd;

    @Column(name = "cardId", length = 20)
    private String cardId;

    @Column(name = "installment")
    private Integer installment;

    @Column(name = "bin", length = 10)
    private String bin;

    @Column(name = "last4", length = 10)
    private String last4;

    @Column(name = "issuer", length = 100)
    private String issuer;

    @Column(name = "cardType", length = 20)
    private String cardType;

    @Column(name = "acquirer", length = 100)
    private String acquirer;

    @Column(name = "webhookUrl", length = 255)
    private String webhookUrl;

    @Column(name = "trxId", length = 30)
    private String trxId;

    @Column(name = "trxType", length = 20)
    private String trxType;

    @Column(name = "tmnId", length = 20)
    private String tmnId;

    @Column(name = "trackId", length = 50)
    private String trackId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "udf1", length = 255)
    private String udf1;

    @Column(name = "udf2", length = 255)
    private String udf2;
} 