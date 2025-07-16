package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_area_index")
    private Integer businessAreaIndex;

    @Column(name = "business_area_id", length = 30)
    private String businessAreaId;

    @Column(name = "business_area_pid", length = 30)
    private String businessAreaPid;

    @Column(name = "business_area_name", length = 30)
    private String businessAreaName;

    @Column(name = "business_area_level")
    private Integer businessAreaLevel;
} 