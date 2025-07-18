package com.jakdang.labs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_index")
    private Integer programIndex;

    @Column(name = "program_name")
    private String programName;

    @Column(name ="program_url")
    private String programUrl;

    @Column(name ="menu_index")
    private Long menuIndex;

    @Column(name="program_value")
    private String programValue;

    @Column(name="program_order")
    private Integer programOrder;

}
