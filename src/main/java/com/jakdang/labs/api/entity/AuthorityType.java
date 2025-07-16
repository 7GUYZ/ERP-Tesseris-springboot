package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authority_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_type_index")
    private Integer authorityTypeIndex;

    @ManyToOne
    @JoinColumn(name = "program_index")
    private Program programIndex;

    @ManyToOne
    @JoinColumn(name = "admin_type_index")
    private adminType adminTypeIndex;

    @Column(name = "insert_authority")
    private Integer insertAuthority;

    @Column(name = "delete_authority")
    private Integer deleteAuthority;

    @Column(name = "update_authority")
    private Integer updateAuthority;

   
} 