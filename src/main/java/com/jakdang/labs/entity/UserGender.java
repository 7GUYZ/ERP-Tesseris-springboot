package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_gender")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_gender_index")
    private Integer userGenderIndex;

    @Column(name = "user_gender_name", length = 6)
    private String userGenderName;
} 