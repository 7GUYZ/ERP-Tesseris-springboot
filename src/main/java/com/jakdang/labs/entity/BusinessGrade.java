package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_grade_index")
    private Integer businessGradeIndex;

    @Column(name = "business_grade_level")
    private Integer businessGradeLevel;

    @Column(name = "business_grade_name", length = 255)
    private String businessGradeName;

    @Column(name = "business_grade_rate")
    private Float businessGradeRate;
} 