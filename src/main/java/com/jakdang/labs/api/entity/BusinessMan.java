package com.jakdang.labs.api.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "`business_man`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessMan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_man_index")
    private Integer businessManIndex;

    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "boss_user_index")
    private Integer bossUserIndex;

    @Column(name = "business_grade_index")
    private Integer businessGradeIndex;

    @Column(name = "business_area_index")
    private Integer businessAreaIndex;

    @Column(name = "business_man_registration_date")
    private LocalDateTime businessManRegistrationDate;
}
