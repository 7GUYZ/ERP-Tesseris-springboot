package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_man")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessMan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_man_index")
    private Integer businessManIndex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "boss_user_index")
    private Integer bossUserIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_grade_index")
    private BusinessGrade businessGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_area_index")
    private BusinessArea businessArea;

    @Column(name = "business_man_distribution_flag")
    private Boolean businessManDistributionFlag;

    @Column(name = "business_man_registration_date")
    private LocalDate businessManRegistrationDate;

    @Column(name = "business_man_create_date")
    private LocalDateTime businessManCreateDate;
} 