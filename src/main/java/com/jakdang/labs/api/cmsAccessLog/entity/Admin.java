package com.jakdang.labs.api.cmsAccessLog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Admin")
@Getter
@Setter
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_index")
    private Integer adminIndex;
    
    @Column(name = "user_index")
    private Integer userIndex;
    
    @Column(name = "admin_type_index")
    private Integer adminTypeIndex;
} 