package com.jakdang.labs.api.cmsAccessLog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Admin_Type")
@Getter
@Setter
public class AdminType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_type_index")
    private Integer adminTypeIndex;
    
    @Column(name = "admin_type_name")
    private String adminTypeName;
} 