package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_index")
    private Integer settingIndex;

    @Column(name = "setting_name", length = 50)
    private String settingName;

    @Column(name = "setting_value", length = 50)
    private String settingValue;
} 