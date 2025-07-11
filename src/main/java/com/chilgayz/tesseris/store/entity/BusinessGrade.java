package com.chilgayz.tesseris.store.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`Business_Grade`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_grade_index")
    private Integer businessGradeIndex;

    @Column(name = "business_grade_name")
    private String businessGradeName;
}
