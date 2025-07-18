package com.jakdang.labs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_index")
    private Integer adminIndex;

    @OneToOne
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "admin_rank_name")
    private String adminRankName;

    @ManyToOne
    @JoinColumn(name = "admin_type_index")
    private adminType adminTypeIndex;

    @Column(name = "admin_registration_date")
    private LocalDateTime adminRegistrationDate;
    
}
