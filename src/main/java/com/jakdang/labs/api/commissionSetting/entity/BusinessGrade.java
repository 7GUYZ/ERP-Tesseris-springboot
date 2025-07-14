package com.jakdang.labs.api.commissionSetting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Business_Grade")
public class BusinessGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_grade_index")
    private Integer businessGradeIndex;

    @Column(name = "business_grade_name")
    private String businessGradeName;

    @Column(name = "business_grade_rate")
    private Float businessGradeRate;

    @Column(name = "business_grade_level")
    private Integer businessGradeLevel;
}
